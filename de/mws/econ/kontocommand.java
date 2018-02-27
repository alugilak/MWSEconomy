package de.mws.econ;

import de.mws.econ.commands.CommandType;
import de.mws.econ.commands.SubCommand;
import de.mws.econ.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class kontocommand implements CommandExecutor
{
    private final List<SubCommand> commands;

    public kontocommand( konto plugin )
    {
        commands = new ArrayList<>();

        commands.add( new balance( plugin ) );
        commands.add( new senden( plugin ) );
        commands.add( new bestenliste( plugin ) );
        commands.add( new hilfe( plugin, this ) );
        commands.add( new erstellen( plugin ) );
        commands.add( new entfernen( plugin ) );
        commands.add( new setzen( plugin ) );
        commands.add( new hinzu( plugin ) );
        commands.add( new abziehen( plugin ) );
        commands.add( new standard( plugin ) );
        commands.add( new reload( plugin ) );
    }

    public List<SubCommand> getCommands()
    {
        return commands;
    }

    private SubCommand getCommand( String name )
    {
        for( SubCommand command : commands )
        {
            String[] aliases = command.getName().split( "," );

            for( String alias : aliases )
            {
                if( alias.equalsIgnoreCase( name ) )
                {
                    return command;
                }
            }
        }

        return null;
    }

    private String[] merge( String[]... arrays )
    {
        int arraySize = 0;

        for( String[] array : arrays )
        {
            arraySize += array.length;
        }

        String[] result = new String[arraySize];

        int j = 0;

        for( String[] array : arrays )
        {
            for( String string : array )
            {
                result[j++] = string;
            }
        }

        return result;
    }

    private void sendDefaultCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        String command = "konto";

        if( !( sender instanceof Player ) && args.length < 1 )
        {
            command = "hilfe";
        }

        onCommand( sender, cmd, commandLabel, merge( new String[]{ command }, args ) );
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 1 )
        {
            sendDefaultCommand( sender, cmd, commandLabel, args );

            return true;
        }

        SubCommand command = getCommand( args[0] );

        if( command == null )
        {
            sendDefaultCommand( sender, cmd, commandLabel, args );

            return true;
        }

        boolean console = !( sender instanceof Player );

        if( console && args.length < 2 && command.getCommandType() == CommandType.CONSOLE_WITH_ARGUMENTS )
        {
            expression.COMMAND_NEEDS_ARGUMENTS.sendWithPrefix( sender );

            return true;
        }

        if( console && command.getCommandType() == CommandType.PLAYER )
        {
        	expression.COMMAND_NOT_CONSOLE.sendWithPrefix( sender, commandLabel );

            return true;
        }

        if( !sender.hasPermission( command.getPermission() ) )
        {
        	expression.NO_PERMISSION_FOR_COMMAND.sendWithPrefix( sender );

            return true;
        }

        String[] realArgs = new String[args.length - 1];

        for( int i = 1; i < args.length; i++ )
        {
            realArgs[i - 1] = args[i];
        }

        if( !command.onCommand( sender, cmd, commandLabel, realArgs ) )
        {
        	expression.TRY_COMMAND.sendWithPrefix( sender, parse( commandLabel, command ) );
        }

        return true;
    }

    public String parse( String label, SubCommand command )
    {
        String commandColor = expression.PRIMARY_COLOR.parse();

        String operatorsColor = expression.PRIMARY_COLOR.parse();

        String argumentColor = expression.ARGUMENT_COLOR.parse();

        StringBuilder builder = new StringBuilder( commandColor ).append( "/" ).append( label );

        if( !command.getFirstName().equalsIgnoreCase( "konto" ) )
        {
            builder.append( " " ).append( command.getFirstName() ).append( " " );
        }

        String[] split = command.getUsage().split( " " );

        if( split[0].equalsIgnoreCase( command.getFirstName() ) )
        {
            for( int i = 1; i < split.length; i++ )
            {
                builder.append( parseArg( split[i], operatorsColor, argumentColor ) ).append( " " );
            }

            builder.deleteCharAt( builder.length() - 1 );
        }
        else
        {
            builder.append( " " ).append( parseArg( split[0], operatorsColor, argumentColor ) ).append( " " );

            builder.deleteCharAt( builder.length() - 1 );
        }

        return builder.toString();
    }

    private String parseArg( String argument, String operatorsColor, String argumentColor )
    {
        String operator = argument.substring( 0, 1 );

        argument = argument.substring( 1, argument.length() );

        String reverse;

        if( operator.equals( "[" ) )
        {
            reverse = "]";
        }
        else
        {
            reverse = ")";
        }

        argument = argument.substring( 0, argument.length() - 1 );

        argument = operatorsColor + operator + argumentColor + argument + operatorsColor + reverse;

        return argument;
    }
}
