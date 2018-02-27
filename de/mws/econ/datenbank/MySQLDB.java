package de.mws.econ.datenbank;

import org.bukkit.configuration.ConfigurationSection;
import de.mws.econ.konto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;

public class MySQLDB extends SQLDB
{
    public MySQLDB( konto plugin )
    {
        super( plugin, true );
    }

    protected Connection getNewConnection()
    {
        ConfigurationSection config = getConfigSection();

        setAccountTable( config.getString( "tables.accounts" ) );

        setAccountsColumnUser( config.getString( "columns.accounts.username" ) );

        setAccountsColumnkonto( config.getString( "columns.accounts.money" ) );

        setAccountsColumnUUID( config.getString( "columns.accounts.uuid" ) );

        try
        {
            Class.forName( "com.mysql.jdbc.Driver" );

            String url = "jdbc:mysql://" + config.getString( "host" ) + ":" + config.getString( "port" ) + "/" + config.getString( "database" );

            return DriverManager.getConnection( url, config.getString( "user" ), config.getString( "password" ) );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    private ConfigurationSection getSection( ConfigurationSection parent, String childName )
    {
        ConfigurationSection child = parent.getConfigurationSection( childName );

        if( child == null )
        {
            child = parent.createSection( childName );
        }

        return child;
    }

    public void getConfigDefaults( ConfigurationSection section )
    {
        section.addDefault( "host", "localhost" );

        section.addDefault( "port", 3306 );

        section.addDefault( "user", "root" );

        section.addDefault( "password", "minecraft" );

        section.addDefault( "database", "MWSmones" );

        ConfigurationSection tables = getSection( section, "tables" );

        tables.addDefault( "accounts", "money_accounts" );

        ConfigurationSection columns = getSection( section, "columns" );

        ConfigurationSection columnsAccounts = getSection( columns, "accounts" );

        columnsAccounts.addDefault( "username", "name" );

        columnsAccounts.addDefault( "money", "money" );

        columnsAccounts.addDefault( "uuid", "uuid" );
    }

    public String getName()
    {
        return "MySQL";
    }

	@Override
	public List<Account> loadTopAccounts(int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Account> getAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> loadAccountData(String name, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void saveAccount(String name, String uuid, double money) {
		// TODO Auto-generated method stub
		
	}
}
