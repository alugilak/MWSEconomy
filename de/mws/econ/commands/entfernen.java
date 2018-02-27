package de.mws.econ.commands;

import de.mws.econ.expression;
import de.mws.econ.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import de.mws.econ.konto;
import de.mws.econ.commands.CommandType;

public class entfernen extends SubCommand
{
    private final konto plugin;

    public entfernen( konto plugin )
    {
        super( "konto-", "konto.konto-", "konto- [name]", expression.COMMAND_REMOVE, CommandType.CONSOLE );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 1 )
        {
            return false;
        }

        String name = args[0];

        if( !plugin.getAPI().accountExists( name, null ) )
        {
        	expression.ACCOUNT_DOES_NOT_EXIST.sendWithPrefix( sender );

            return true;
        }

        plugin.getAPI().removeAccount( name, null );

        expression.ACCOUNT_REMOVED.sendWithPrefix( sender, expression.PRIMARY_COLOR.parse() + name + expression.SECONDARY_COLOR.parse() );

        return true;
    }
}
