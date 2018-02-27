package de.mws.econ.commands;

import de.mws.econ.expression;
import de.mws.econ.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.mws.econ.konto;
import de.mws.econ.kontocommand;
import de.mws.econ.commands.CommandType;

public class hilfe extends SubCommand
{
    private final konto plugin;

    private final kontocommand command;

    public hilfe( konto plugin, kontocommand command )
    {
        super( "hilfe,?", "konto.?", "hilfe", expression.COMMAND_HELP, CommandType.CONSOLE );

        this.plugin = plugin;

        this.command = command;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        sender.sendMessage( plugin.getEqualMessage( expression.HELP.parse(), 10 ) );

        String operatorColor = expression.TERTIARY_COLOR.parse();

        String textColor = expression.SECONDARY_COLOR.parse();

        sender.sendMessage( textColor + expression.HELP_ARGUMENTS.parse( operatorColor + "[]" + textColor, operatorColor + "()" + textColor ) );

        for( SubCommand command : this.command.getCommands() )
        {
            if( command.getName().equalsIgnoreCase( getName() ) )
            {
                continue;
            }

            if( !sender.hasPermission( command.getPermission() ) )
            {
                continue;
            }

            if( !( sender instanceof Player ) && command.getCommandType() == CommandType.PLAYER )
            {
                continue;
            }

            sender.sendMessage( this.command.parse( commandLabel, command ) + textColor + " - " + command.getDescription().parse() );
        }

        sender.sendMessage( plugin.getEndEqualMessage( 27 ) );

        return true;
    }
}
