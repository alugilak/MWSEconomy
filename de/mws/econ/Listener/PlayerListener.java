package de.mws.econ.Listener;

import de.mws.econ.konto;
import de.mws.econ.datenbank.Account;
import de.mws.econ.datenbank.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private final konto plugin;

    public PlayerListener( konto plugin )
    {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

	@EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerLogin( PlayerLoginEvent event )
    {
        Player player = event.getPlayer();
        plugin.getAPI().updateAccount( player.getName(), player.getUniqueId().toString() );
    }

	   @EventHandler
	    public void onPlayerQuit( PlayerQuitEvent event )
	    {
	        Database database = plugin.getkontoDatabase();

	        Player player = event.getPlayer();

	        Account account = database.getCachedAccount( player.getName(), player.getUniqueId().toString() );

	        if( account != null )
	        {
	            account.save( account.getkonto() );

	            database.removeCachedAccount( account );
	        }
	    }
	}
