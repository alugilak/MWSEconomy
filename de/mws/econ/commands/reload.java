package de.mws.econ.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.mws.econ.konto;
import de.mws.econ.expression;
import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;

public class reload extends SubCommand
{
    private final konto plugin;

    public reload( konto plugin )
    {
        super( "reload", "konto.reload", "reload", expression.COMMAND_RELOAD, CommandType.CONSOLE );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        plugin.reloadConfig();

        expression.CONFIG_RELOADED.sendWithPrefix( sender );

        return true;
    }
}
