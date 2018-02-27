package de.mws.econ.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.datenbank.Account;

public class senden extends SubCommand
{
    private final konto plugin;

    public senden( konto plugin )
    {
        super( "senden,geben,schenken", "konto.senden", "senden [name] [menge]", expression.COMMAND_SEND, CommandType.PLAYER );

        this.plugin = plugin;
    }

 
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 2 )
        {
            return false;
        }

        double money;

        try
        {
            money = Double.parseDouble( args[1] );
        }
        catch( NumberFormatException e )
        {
            return false;
        }

        if( money <= 0.0 )
        {
            return false;
        }

        Account receiver = plugin.getShortenedAccount( args[0] );

        if( receiver == null )
        {
        	expression.ACCOUNT_DOES_NOT_EXIST.sendWithPrefix( sender );

            return true;
        }

        Account account = plugin.getAPI().getAccount( sender.getName(), null );

        if( !account.has( money ) )
        {
        	expression.NOT_ENOUGH_konto.sendWithPrefix( sender );

            return true;
        }

        if( !receiver.canReceive( money ) )
        {
        	expression.MAX_BALANCE_REACHED.sendWithPrefix( sender, receiver.getName() );

            return true;
        }

        String formattedMoney = plugin.getAPI().format( money );

        account.withdraw( money );

        receiver.deposit( money );

        expression.KONTO_SENT.sendWithPrefix( sender, formattedMoney, receiver.getName() );

        Player receiverPlayer = plugin.getServer().getPlayerExact( receiver.getName() );

        if( receiverPlayer != null )
        {
        	expression.KONTO_RECEIVE.sendWithPrefix( receiverPlayer, formattedMoney, sender.getName() );
        }

        return true;
    }
}
