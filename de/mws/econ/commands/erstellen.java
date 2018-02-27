package de.mws.econ.commands;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.datenbank.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class erstellen extends SubCommand
{
    private final konto plugin;

    public erstellen( konto plugin )
    {
        super( "erstellen", "konto.erstellen", "erstellen [name]", expression.COMMAND_CREATE, CommandType.CONSOLE );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 1 )
        {
            return false;
        }

        String name = args[0];

        if( plugin.getAPI().accountExists( name, null ) )
        {
        	expression.ACCOUNT_EXISTS.sendWithPrefix( sender );

            return true;
        }

        if( name.length() > 16 )
        {
        	expression.NAME_TOO_LONG.sendWithPrefix( sender );

            return true;
        }

        Account account = plugin.getAPI().updateAccount( name, null );

        expression.ACCOUNT_CREATED.sendWithPrefix( sender, expression.PRIMARY_COLOR.parse() + account.getName() + expression.SECONDARY_COLOR.parse() );

        return true;
    }
}
