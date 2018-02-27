package de.mws.econ.commands;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.datenbank.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hinzu extends SubCommand
{
    private final konto plugin;

    public hinzu( konto plugin )
    {
        super( "+", "konto.+", "+ [name] [menge]", expression.COMMAND_GRANT, CommandType.CONSOLE );

        this.plugin = plugin;
    }


    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 2 )
        {
            return false;
        }

        double konto;

        try
        {
            konto = Double.parseDouble( args[1] );
        }
        catch( NumberFormatException e )
        {
            return false;
        }

        Account victim = plugin.getShortenedAccount( args[0] );

        if( victim == null )
        {
        	expression.ACCOUNT_DOES_NOT_EXIST.sendWithPrefix( sender );
            return true;
        }

        if( !victim.canReceive( konto ) )
        {
        	expression.MAX_BALANCE_REACHED.sendWithPrefix( sender, victim.getName() );
            return true;
        }

        String formattedkonto = plugin.getAPI().format( konto );

        victim.deposit( konto );

        expression.PLAYER_GRANT_konto.sendWithPrefix( sender, formattedkonto, victim.getName() );

        Player receiverPlayer = plugin.getServer().getPlayerExact( victim.getName() );

        if( receiverPlayer != null )
        {
        	expression.PLAYER_GRANTED_konto.sendWithPrefix( receiverPlayer, formattedkonto, sender.getName() );
        }

        return true;
    }
}	
