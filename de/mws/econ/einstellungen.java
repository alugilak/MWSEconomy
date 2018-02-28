package de.mws.econ;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
public class einstellungen
{
	static einstellungen instanz = new einstellungen();
	  Plugin p;
	  FileConfiguration data ;
	  File dfile;
	  
	  FileConfiguration b2data ;
	  File b2;
	konto plugin;
	  public static einstellungen getInstance()
	  {
	    return instanz;
	  }
	  
	  public void setup(Plugin p) 
	  {
	    if (!p.getDataFolder().exists()) {
	      p.getDataFolder().mkdir();
	      }
	    this.dfile = new File(p.getDataFolder(), "Playerbank.yml");

	    this.b2 = new File(p.getDataFolder(), "Vipbank.yml");
	    
	    
	        this.b2data = YamlConfiguration.loadConfiguration(this.b2);
	        
	        {
	        	if (!this.b2.exists()) {
	                try
	                {
	                  this.b2.createNewFile();
	                }
	                catch (IOException d)
	                {
	                  Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann die datei nicht lesen bank2.yml!");
	                }
	              }
	              
	 this.data = YamlConfiguration.loadConfiguration(this.dfile);
	    if (!this.dfile.exists()) {
	      try
	      {
	        this.dfile.createNewFile();
	      }
	      catch (IOException d)
	      {
	        Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann die datei nicht lesen warps_worlds/warpsAndspawns.yml!");
	      }
	    }}}
	   
	    
	  
	  public FileConfiguration getb2Data()
	  {
	      return this.b2data;
	    } 
	      
	  public FileConfiguration getData()
	  {
	    return this.data;
	  }
	  
	  public void saveb2Data()
	  {
	    try
	    {
	      this.b2data.save(this.b2);
	    }
	    catch (IOException d)
	    {
	      Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann Datei nicht speichern bank2.yml!");
	    }}
	  
	  public void saveData()
	  {
	    try
	    {
	      this.data.save(this.dfile);
	    }
	    catch (IOException d)
	    {
	      Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann Datei nicht speichern warps_worlds/warpsAndspawns.yml!");
	    }}
	  public PluginDescriptionFile getDesc()
	  {
	    return this.p.getDescription();
	  }
	  public void reloadb2Data()
	  {
	    this.b2data = YamlConfiguration.loadConfiguration(this.b2);
	  }
	  public void reloadData()
	  {
	    this.data = YamlConfiguration.loadConfiguration(this.dfile);
	  }
	  public void saveb2DefaultConfig() {
			
		  this.b2data = YamlConfiguration.loadConfiguration(this.b2);
	  }
	  
	  public void saveDefaultConfig() {
			
		  this.data = YamlConfiguration.loadConfiguration(this.dfile);
	  }
	}


