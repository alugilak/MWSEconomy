package de.mws.econ;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.lang.reflect.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import de.mws.econ.expression;
import de.mws.econ.datenbank.Account;
import de.mws.econ.Listener.PlayerListener;
import de.mws.econ.datenbank.Database;
import de.mws.econ.datenbank.MySQLDB;
import de.mws.econ.datenbank.SQLiteDB;
import de.mws.econ.einstellungen;
import de.mws.econ.VaultHandler.VaultHandler;


import net.milkbowl.vault.economy.Economy;

public class konto  extends JavaPlugin implements Listener
{   private static HashMap<Player, Inventory> playerBank = new HashMap<Player, Inventory>();
    private static HashMap<Player, Inventory> playerBank2 = new HashMap<Player, Inventory>();
    public static Inventory inventory = null;    

    
public einstellungen settings = einstellungen.getInstance();
FileConfiguration data;
File dfile;
FileConfiguration b2data ;
File b2;

    private final Set<Database> databases;
    private API api;
    private Database database;

    public konto()
    {
        databases = new HashSet<>();
    }

    public void onEnable()
    {	        getDataFolder().mkdirs();
        expression.init( this );
        
        this.data = getConfig();        
        this.saveDefaultConfig();
        
        this.data.options().copyDefaults(true);
        this.b2data = getConfig();        
        this.b2data.options().copyDefaults(true);
        this.settings.setup(this);
        this.settings.saveData();
        this.settings.saveb2Data();
       
        databases.add( new MySQLDB( this ) );
        databases.add( new SQLiteDB( this ) );
        saveDefaultConfig();
        this.saveConfig();
	    saveConfig();	    
	    
	    	
	    this.settings.reloadData();
	    this.settings.reloadb2Data();
	  	    
		  org.bukkit.plugin.PluginManager pm1 = org.bukkit.Bukkit.getPluginManager();		  
		  pm1.registerEvents(this, this); 
		  checkUpdate();
	    getServer().getPluginManager().registerEvents(this, this); 
        for( Database database : databases )
        {
            String name = database.getConfigName();

            ConfigurationSection section = getConfig().getConfigurationSection( name );

            if( section == null )
            {
                section = getConfig().createSection( name );
            }

            database.getConfigDefaults( section );

            if( section.getKeys( false ).isEmpty() )
            {
                getConfig().set( name, null );
            }
        }

        getConfig().options().copyDefaults( true );

        getConfig().options().header( "MWSKonto Config - Fe recode\n" +
                "holdings - Der Kontostand mit dem Spieler starten\n" +
                "prefix - Was im Chat vor allem stehen soll wenn MWS Konto Mitteilungen sendet\n" +
                "currency - Der Name der Währung\n" +
                "type - Die Datenbank die genutzt wird (sqlite, mysql, or mongo)" );
        saveConfig();

        api = new API( this );

        if( !setupDatabase() )
        {
            return;
        }

        getCommand( "konto" ).setExecutor( new kontocommand( this ) );

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents( new PlayerListener( this ), this );

        setupVault();

        reloadConfig();

        // Auto Clean On Startup
        if( api.isAutoClean() )
        {
            api.clean();
            log( expression.ACCOUNT_CLEANED );
        }
    }

	
	  public static String updater = "MultiWorldSystemKonto";
	  public static int resource = 53164;
	  public void checkUpdate() {
	  	System.out.println(updater + "Checking for updates...");
	  	try {
	              HttpURLConnection con = (HttpURLConnection) new URL(
	                      "http://www.spigotmc.org/api/general.php").openConnection();
	              con.setDoOutput(true);
	              con.setRequestMethod("POST");
	              con.getOutputStream()
	                      .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&53164=" + resource)
	                              .getBytes("UTF-8"));
	              String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	              if (!version.equalsIgnoreCase(this.getDescription().getVersion())) {
	                  System.err.println(updater + "A new update is aviable: version " + version);
	              } else {
	              	System.out.println(updater + "You're running the newest plugin version!");
	              }
	          } catch (Exception ex) {
	             System.err.println(updater + "Failed to check for updates on spigotmc.org");
	          }
	  }
	  public String getLanguage(Player p) throws Throwable, IllegalAccessException{
		  Object ep = getMethod("getHandle", p.getClass()).invoke(p, (Object[]) null);
		  Field f = ep.getClass().getDeclaredField("locale");
		  f.setAccessible(true);
		  String language = (String) f.get(ep);
		  return language;
		  }
		  private Method getMethod(String name, Class<?> clazz) {
		  for (Method m : clazz.getDeclaredMethods()) {
		  if (m.getName().equals(name))
		  return m;
		  }
		  return null;
		  }
		  
		  public static HashMap<Player, Inventory> getPlayerBank()
		  {
		    return playerBank;
		  }
		  public static HashMap<Player, Inventory> getPlayerBank2()
		  {
		    return playerBank2;
		  }

		 
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks( this );

        getkontoDatabase().close();
    }

    public void log( String message )
    {
        getLogger().info( message );
    }

    public void log( expression expression, String... args )
    {
        log( expression.parse( args ) );
    }

    public Database getkontoDatabase()
    {
        return database;
    }

    public boolean addDatabase( Database database )
    {
        return databases.add( database );
    }

    public Set<Database> getDatabases()
    {
        return new HashSet<>( databases );
    }

    public API getAPI()
    {
        return api;
    }

    private boolean setupDatabase()
    {
        String type = getConfig().getString( "type" );

        database = null;

        for( Database database : databases )
        {
            if( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                this.database = database;

                break;
            }
        }

        if( database == null )
        {
            log( expression.DATABASE_TYPE_DOES_NOT_EXIST );

            return false;
        }

        if( !database.init() )
        {
            log( expression.DATABASE_FAILURE_DISABLE );

            setEnabled( false );

            return false;
        }

        return true;
    }

    private void setupexpressions()
    {
        File expressionsFile = new File( getDataFolder(), "messages.yml" );

        for( expression expression : expression.values() )
        {
            expression.reset();
        }

        if( !expressionsFile.exists() )
        {
            return;
        }

        YamlConfiguration expressionsConfig = YamlConfiguration.loadConfiguration( expressionsFile );

        for( expression expression : expression.values() )
        {
            String expressionConfigName = expression.getConfigName();

            String expressionMessage = expressionsConfig.getString( expressionConfigName );

            if( expressionMessage == null )
            {
                expressionMessage = expression.parse();
            }

            expression.setMessage( expressionMessage );
        }
    }
   




    public void reloadConfig()
    {
        super.reloadConfig();

        String oldCurrencySingle = getConfig().getString( "currency.single" );

        String oldCurrencyMultiple = getConfig().getString( "currency.multiple" );

        if( oldCurrencySingle != null )
        {
            getConfig().set( "currency.major.single", oldCurrencySingle );

            getConfig().set( "currency.single", null );
        }

        if( oldCurrencyMultiple != null )
        {
            getConfig().set( "currency.major.multiple", oldCurrencyMultiple );

            getConfig().set( "currency.multiple", null );
        }

        if( !getConfig().isSet( "autoclean" ) )
        {
            getConfig().set( "autoclean", true );
        }

        // Temporarily remove cache and updates.
        if( getConfig().isSet( "cacheaccounts" ) )
        {
            getConfig().set( "cacheaccounts", null );
        }
        if( getConfig().getBoolean( "updatecheck" ) )
        {
            getConfig().set( "updatecheck", null );
        }

        setupexpressions();

        saveConfig();
    }

    public Account getShortenedAccount( String name )
    {
        Account account = getAPI().getAccount( name, null );

        if( account == null )
        {
            Player player = getServer().getPlayer( name );

            if( player != null )
            {
                account = getAPI().getAccount( player.getName(), null );
            }
        }

        return account;
    }

    public String getMessagePrefix()
    {
        String third = expression.TERTIARY_COLOR.parse();

        return third + "[" + expression.PRIMARY_COLOR.parse() + "$1" + third + "] " + expression.SECONDARY_COLOR.parse();
    }

    public String getEqualMessage( String inBetween, int length )
    {
        return getEqualMessage( inBetween, length, length );
    }

    public String getEqualMessage( String inBetween, int length, int length2 )
    {
        String equals = getEndEqualMessage( length );

        String end = getEndEqualMessage( length2 );

        String third = expression.TERTIARY_COLOR.parse();

        return equals + third + "[" + expression.PRIMARY_COLOR.parse() + inBetween + third + "]" + end;
    }

    public String getEndEqualMessage( int length )
    {
        String message = expression.SECONDARY_COLOR.parse() + "";

        for( int i = 0; i < length; i++ )
        {
            message += "=";
        }

        return message;
    }

    private void setupVault()
    {
        Plugin vault = getServer().getPluginManager().getPlugin( "Vault" );

        if( vault == null )
        {
            return;
        }

        getServer().getServicesManager().register( Economy.class, new VaultHandler( this ), this, ServicePriority.Highest );
    }
    
	  @EventHandler
	  public void entityDamageEvent(EntityDamageEvent event)
	  {
	    if (((event.getEntity() instanceof Player)) && (getPlayerBank().containsKey(event.getEntity()))) {
	      if (getConfig().getBoolean("PlayerImmuneInBank")) {
	        event.setCancelled(true);}}
	    if (((event.getEntity() instanceof Player)) && (getPlayerBank2().containsKey(event.getEntity()))) {
	        if (getConfig().getBoolean("PlayerImmuneInVipBank")) {
	          event.setCancelled(true);}}}
	  @EventHandler
	  public void PlayerJoin(PlayerLoginEvent event) {	
		  getConfig().set(event.getPlayer().getUniqueId().toString() + event.getPlayer() + ".Pslots", 6);
		int b1 = getConfig().getInt(event.getPlayer().getUniqueId().toString()+ event.getPlayer() + ".Pslots" );		  
		  if (!this.settings.getData().contains(event.getPlayer().getUniqueId().toString())) {
		      for (int slotIndex = 0; slotIndex != b1; slotIndex++)
		      {		this.settings.getData().set(event.getPlayer().getUniqueId().toString() + ".item" + slotIndex, new ItemStack(Material.AIR));
		    	  this.settings.saveData();}
		    	  			  
			int b2 = getConfig().getInt(event.getPlayer().getUniqueId().toString()+ event.getPlayer() + ".Vslots" );	
		      if (!this.settings.getb2Data().contains(event.getPlayer().getUniqueId().toString())) {
		    	  getConfig().set(event.getPlayer().getUniqueId().toString() + event.getPlayer() + ".Vslots", 6);
			      for (int slotIndex1 = 0; slotIndex1 != b2; slotIndex1++)
			      {
			    	  this.settings.getb2Data().set(event.getPlayer().getUniqueId().toString() + ".item" + slotIndex1, new ItemStack(Material.AIR));			    	  
			    	  this.settings.saveb2Data();}}}}
	  
	  @EventHandler
	  public void inventoryCloseEvent(InventoryCloseEvent event)
	  {
	    if (((event.getPlayer() instanceof Player)) && 
	      (getPlayerBank().containsKey(event.getPlayer())))
	    {
	    	
		 int b1 = getConfig().getInt(event.getPlayer().getUniqueId().toString()+ event.getPlayer() + ".Pslots" );
	      Inventory inventory = getPlayerBank().get(event.getPlayer());
	      for (int slotIndex = 0; slotIndex != b1; slotIndex++)
	      {
	        ItemStack itemStack;	        
	        if (inventory.getItem(slotIndex) == null) {
	          itemStack = new ItemStack(Material.AIR);
	        } else {
	          itemStack = new ItemStack(inventory.getItem(slotIndex));
	        }
	        this.settings.getData().set(event.getPlayer().getUniqueId().toString() + ".item" + slotIndex, itemStack);
	        this.settings.saveData();
	      }
	      event.getPlayer().sendMessage(ChatColor.YELLOW + getConfig().getString("PlayerBankSaveMessageTitel") + ChatColor.AQUA + " " + event.getPlayer().getName());
	      
	      getPlayerBank().remove(event.getPlayer());
	    }
	    if (((event.getPlayer() instanceof Player)) && 
	  	      (getPlayerBank2().containsKey(event.getPlayer())))
	  	    {
			 int b2 = getConfig().getInt(event.getPlayer().getUniqueId().toString()+ event.getPlayer() + ".Vslots" );
	  	      Inventory inventory1 = (Inventory)getPlayerBank2().get(event.getPlayer());
	  	      for (int slotIndex1 = 0; slotIndex1 != b2; slotIndex1++)
	  	      {
	  	        ItemStack itemStack1;	        
	  	        if (inventory1.getItem(slotIndex1) == null) {
	  	          itemStack1 = new ItemStack(Material.AIR);
	  	        } else {
	  	          itemStack1 = new ItemStack(inventory1.getItem(slotIndex1));
	  	        }
	  	        this.settings.getb2Data().set(event.getPlayer().getUniqueId().toString() + ".item" + slotIndex1, itemStack1);
	  	        this.settings.saveb2Data();
	  	      }
	  	      event.getPlayer().sendMessage(ChatColor.YELLOW + getConfig().getString("VipBankSaveMessageTitel") + ChatColor.AQUA + " " + event.getPlayer().getName());
	  	      
	  	      getPlayerBank2().remove(event.getPlayer());}
	  	    }
	  public static FileConfiguration loadYaml(JavaPlugin instance, String path) {
		    if(instance == null) {
		        throw new IllegalArgumentException("Instance of the plugin is null!");
		    } else if(path != null && !path.equals("")) {
		        if(!path.endsWith(".yml")) {
		            path = path + ".yml";
		        }

		        boolean tab = path.toLowerCase().contains("tabs" + File.separator);
		        File file = new File(instance.getDataFolder(), path);
		        if(file.isDirectory()) {
		            file.delete();
		        }

		        if(!file.exists()) {
		            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Can't find file \"" + path + "\"");
		            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Creating some one for you...");

		            try {
		                instance.saveResource(path, true);
		                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Completed!");
		            } catch (Exception var7) {
		                if(!tab) {
		                    instance.getLogger().log(Level.SEVERE, "Failed to load resourse " + path);
		                    return null;
		                }

		                try {
		                    file.createNewFile();
		                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Completed!");
		                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "File will be empty Fill it yourself!");
		                    return YamlConfiguration.loadConfiguration(file);
		                } catch (IOException var6) {
		                    return null;
		                }
		            }
		        }

		        return YamlConfiguration.loadConfiguration(file);
		    } else {
		        throw new IllegalArgumentException("Path can not be null or empty!");
		    }
		}
		            
		        
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {Player p = (Player)sender;
    if (cmd.getName().equalsIgnoreCase("pib"))
    {
    if (!(p.hasPermission(getConfig().getString("mwsbanking")) || (p.hasPermission(getConfig().getString("mwsbanking" + ".*")))))       
        {
      	  sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + (getConfig().getString("SystemName")) + ChatColor.GOLD.toString() + ChatColor.BOLD + " >" + ChatColor.BLUE + (getConfig().getString("mwsbanking")) + " " + ChatColor.GREEN + (getConfig().getString("permErrorpb")));
          p.getWorld().playEffect(p.getLocation(), Effect.GHAST_SHRIEK, 50);
          return false;
        }  else {  
  	  int b1 = getConfig().getInt(p.getUniqueId().toString() + p.getPlayer() + ".Pslots")  ;
      	  Inventory inventory = Bukkit.createInventory(p, b1, getConfig().getString("BankName"));
        for (int slotIndex = 0; slotIndex != b1; slotIndex++)
          inventory.setItem(slotIndex, this.settings.getData().getItemStack(p.getUniqueId().toString() + ".item" + slotIndex));      
               p.openInventory(inventory);
               playerBank.put(p, inventory);
      
    
  	return true;}

    }
    if (cmd.getName().equalsIgnoreCase("vib")) 
    { if ((sender instanceof Player))  {       
    	this.settings.reloadb2Data();
        if (!(p.hasPermission(getConfig().getString("mwsvipbanking")) || (p.hasPermission(getConfig().getString("mwsvipbanking" + ".*")))))
        {
      	  sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + (getConfig().getString("SystemName")) + ChatColor.GOLD.toString() + ChatColor.BOLD + " >" + ChatColor.BLUE + (getConfig().getString("mwsvipbanking")) + " " + ChatColor.GREEN + (getConfig().getString("permErrorv")));
          p.getWorld().playEffect(p.getLocation(), Effect.GHAST_SHRIEK, 50);
          return false;
        }  else { 
  	 int b2 = getConfig().getInt(p.getUniqueId().toString() + p.getPlayer() + ".Vslots");
      	  Inventory inventory1 = Bukkit.createInventory(p, b2, getConfig().getString("VipBankName"));
        for (int slotIndex1 = 0; slotIndex1 != b2; slotIndex1++)
          inventory1.setItem(slotIndex1, this.settings.getb2Data().getItemStack(p.getUniqueId().toString() + ".item" + slotIndex1));      
        p.openInventory(inventory1);
        playerBank2.put(p, inventory1);        
  	return true;
    }}}
    if (cmd.getName().equalsIgnoreCase("bankadmin")) 
    { if ((sender instanceof Player))  {    	    
        if (!sender.hasPermission(getConfig().getString("mwsadminbanking")))
        {
      	  sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + (getConfig().getString("SystemName")) + ChatColor.GOLD.toString() + ChatColor.BOLD + " >" + ChatColor.BLUE + (getConfig().getString("mwsvipbanking")) + " " + ChatColor.GREEN + (getConfig().getString("permErrorv")));
          p.getWorld().playEffect(p.getLocation(), Effect.GHAST_SHRIEK, 50);
          return false;
        }}if (args.length ==0) {        		
        	p.sendMessage(ChatColor.GREEN +  "*****************" + ChatColor.GOLD + "MWS-ITEM-BANK-ADMINMENU-" + ChatColor.GREEN + "*******************");
  		  p.sendMessage(ChatColor.AQUA+ "Set Slot for every Players Item Bank!");
  		  p.sendMessage(ChatColor.GREEN +  "************************************************************");
  		  p.sendMessage(ChatColor.AQUA+ "For Player Item Bank use " + ChatColor.DARK_PURPLE + " /bankadmin pb <PlayerName> <SlotAmount> !");
  		  p.sendMessage(ChatColor.AQUA+ "************************************************************");
  		  p.sendMessage(ChatColor.AQUA+ "For VIP Player Item Bank use " + ChatColor.DARK_PURPLE + " /bankadmin vb <PlayerName> <SlotAmount> ");  		 
  		  p.sendMessage(ChatColor.GREEN +  "************************************************************");
  		  p.sendMessage(ChatColor.AQUA+ "Permissions Playerbank:" + ChatColor.DARK_PURPLE + " konto.pbank konto.pbank.* ");
  	   	p.sendMessage(ChatColor.AQUA+ "Permissions VIP-Playerbank:" + ChatColor.DARK_PURPLE + " konto.vbank konto.vbank.* "); 
  	    p.sendMessage(ChatColor.AQUA+ "Permissions Bankadmin:" + ChatColor.DARK_PURPLE + " konto.bank.admin "); 
		  p.sendMessage(ChatColor.GREEN +  "************************************************************");}  else {         	       
        	if (args.length >=2) 
        		if (args[0].equalsIgnoreCase("pb")) {
              	int Slots = Integer.parseInt(args[2]); 
              	Player target = Bukkit.getServer().getPlayer(args[1]);
              	getConfig().set(target.getPlayer().getUniqueId().toString() + target.getPlayer() + ".Pslots", Slots);                               
              	saveConfig();
                reloadConfig();{
		    		p.sendMessage("Player Item Bank Slots gespeichert");
     return true;}}else {  
                if (args.length >=2) 
            		if (args[0].equalsIgnoreCase("vb")) {
                  	int VSlots = Integer.parseInt(args[2]); 
                  	Player Vtarget = Bukkit.getServer().getPlayer(args[1]);
                  	getConfig().set(Vtarget.getPlayer().getUniqueId().toString() + Vtarget.getPlayer() + ".Vslots", VSlots);                               
                  	saveConfig();
                    reloadConfig();{
    		    		p.sendMessage("Vip Player Item Bank Slots gespeichert");
       return true; }
}
	return true;}}}
	return false;
	}}

