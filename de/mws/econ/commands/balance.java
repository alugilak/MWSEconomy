package de.mws.econ.commands;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.datenbank.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class balance extends SubCommand
{
    private final konto plugin;

    public balance( konto plugin )
    {
        super( "konto,bal", "konto.konto", "(name)", expression.COMMAND_BALANCE, CommandType.CONSOLE_WITH_ARGUMENTS );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        Account account;

        if( args.length > 0 && sender.hasPermission( "money.balance.other" ) )
        {
            account = plugin.getShortenedAccount( args[0] );

            if( account == null )
            {
            	expression.ACCOUNT_DOES_NOT_EXIST.sendWithPrefix( sender );

                return true;
            }

            expression.ACCOUNT_HAS.sendWithPrefix( sender, account.getName(), plugin.getAPI().format( account ) );
        }
        else
        {
            Player player = plugin.getServer().getPlayer( sender.getName() );

            account = plugin.getAPI().getAccount( sender.getName(), player != null ? player.getUniqueId().toString() : null );

            if( account == null )
            {
            	expression.YOUR_ACCOUNT_DOES_NOT_EXIST.sendWithPrefix( sender );

                return true;
            }

            expression.YOU_HAVE.sendWithPrefix( sender, plugin.getAPI().format( account ) );
        }

        return true;
    }
}
