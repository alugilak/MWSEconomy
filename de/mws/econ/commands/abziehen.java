package de.mws.econ.commands;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.datenbank.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.mws.econ.commands.CommandType;

public class abziehen extends SubCommand
{
    private final konto plugin;

    public abziehen( konto plugin )
    {
        super( "-", "konto.-", "- [name] [menge]", expression.COMMAND_DEDUCT, CommandType.CONSOLE );

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

        String formattedkonto = plugin.getAPI().format( konto );

        victim.withdraw( konto );

        expression.PLAYER_DEDUCT_konto.sendWithPrefix( sender, formattedkonto, victim.getName() );

        Player receiverPlayer = plugin.getServer().getPlayerExact( victim.getName() );

        if( receiverPlayer != null )
        {
        	expression.PLAYER_DEDUCTED_konto.sendWithPrefix( receiverPlayer, formattedkonto, sender.getName() );
        }

        return true;
    }
}	
