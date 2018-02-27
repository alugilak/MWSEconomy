package de.mws.econ.datenbank;

import org.bukkit.configuration.ConfigurationSection;
import de.mws.econ.konto;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteDB extends SQLDB
{
    private final konto plugin;

    public SQLiteDB( konto plugin )
    {
        super( plugin, false );

        this.plugin = plugin;
    }

    public Connection getNewConnection()
    {
        try
        {
            Class.forName( "org.sqlite.JDBC" );

            return DriverManager.getConnection( "jdbc:sqlite:" + new File( plugin.getDataFolder(), "database.db" ).getAbsolutePath() );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    public void getConfigDefaults( ConfigurationSection section )
    {

    }

    public String getName()
    {
        return "SQLite";
    }
}
