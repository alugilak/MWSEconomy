package de.mws.econ.datenbank;

import de.mws.econ.API;
import de.mws.econ.konto;

public class Account
{
    private final konto plugin;

    private String name;

    private final String uuid;

    private final API api;

    private final Database database;

    private Double konto;

    public Account( konto plugin, String name, String uuid, Database database )
    {
        this.plugin = plugin;

        this.name = name;

        this.uuid = uuid;

        this.api = plugin.getAPI();

        this.database = database;

        this.konto = null;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;

        if( konto == null )
        {
            this.konto = getkonto();
        }

        database.saveAccount( name, uuid, konto );
    }

    public String getUUID()
    {
        return uuid;
    }

    public Double getkonto()
    {
        if( konto != null )
        {
            return konto;
        }

        String konto_string = database.loadAccountData( name, uuid ).get( "konto" );
        Double konto;

        try
        {
            konto = Double.parseDouble( konto_string );
        }
        catch( Exception e )
        {
            return 0D;
        }

        if( database.cacheAccounts() )
        {
            this.konto = konto;
        }

        return konto;
    }

    public void setkonto( double konto )
    {
        Double currentkonto = getkonto();

        if( currentkonto != null && currentkonto == konto )
        {
            return;
        }

        if( konto < 0 && !api.isCurrencyNegative() )
        {
            konto = 0;
        }

        currentkonto = api.getkontoRounded( konto );

        if( api.getMaxHoldings() > 0 && currentkonto > api.getMaxHoldings() )
        {
            currentkonto = api.getkontoRounded( api.getMaxHoldings() );
        }

        if( !database.cacheAccounts() || plugin.getServer().getPlayerExact( getName() ) == null )
        {
            save( currentkonto );
        }
        else
        {
            this.konto = currentkonto;
        }
    }

    public void withdraw( double amount )
    {
        setkonto( getkonto() - amount );
    }

    public void deposit( double amount )
    {
        setkonto( getkonto() + amount );
    }

    public boolean canReceive( double amount )
    {
        return api.getMaxHoldings() == -1 || amount + getkonto() < api.getMaxHoldings();

    }

    public boolean has( double amount )
    {
        return getkonto() >= amount;
    }

    public void save( double konto )
    {
        database.saveAccount( name, uuid, konto );
    }

    @Override
    public boolean equals( Object object )
    {
        if( !( object instanceof Account ) )
        {
            return false;
        }

        Account account = ( Account ) object;

        return account.getName().equals( getName() );
    }
}
