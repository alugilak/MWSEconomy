package de.mws.econ.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.mws.econ.konto;
import de.mws.econ.datenbank.Account;
import de.mws.econ.expression;

public class bestenliste extends SubCommand
{
    private final konto plugin;

    public bestenliste( konto plugin )
    {
        super( "top", "konto.top", "top", expression.COMMAND_TOP, CommandType.CONSOLE );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        List<Account> topaccounts = plugin.getAPI().getTopAccounts();

        if( topaccounts.size() < 1 )
        {
            expression.NO_ACCOUNTS_EXIST.sendWithPrefix( sender );

            return true;
        }

        sender.sendMessage( plugin.getEqualMessage( expression.RICH.parse(), 10 ) );

        for( int i = 0; i < topaccounts.size(); i++ )
        {
            Account account = topaccounts.get( i );

            String two = expression.SECONDARY_COLOR.parse();

            sender.sendMessage( two + ( i + 1 ) + ". " + expression.PRIMARY_COLOR.parse() + account.getName() + two + " - " + plugin.getAPI().format( account ) );
        }

        sender.sendMessage( plugin.getEndEqualMessage( 28 ) );

        return true;
    }
}
