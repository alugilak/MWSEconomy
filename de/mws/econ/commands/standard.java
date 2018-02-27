package de.mws.econ.commands;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class standard extends SubCommand
{
    private final konto plugin;

    public standard( konto plugin )
    {
        super( "standard", "konto.standard", "standard", expression.COMMAND_CLEAN, CommandType.CONSOLE );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        plugin.getAPI().clean();

        expression.ACCOUNT_CLEANED.sendWithPrefix( sender );

        return true;
    }
}
