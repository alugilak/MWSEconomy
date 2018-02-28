package de.mws.econ;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import de.mws.econ.einstellungen;
import de.mws.econ.konto;


	  

public enum expression  {
    DATABASE_TYPE_DOES_NOT_EXIST("Dieser Datenbanktyp existiert nicht." ),
    DATABASE_FAILURE_DISABLE( "Die Initialisierung der Datenbank ist fehlgeschlagen und MWSKonto wurde deaktiviert." ),
    COMMAND_NEEDS_ARGUMENTS( "Dieser Befehl braucht Argumente." ),
    COMMAND_NOT_CONSOLE( "Der Befehl '1$' kann nicht in der Konsole verwendet werden." ),
    NO_PERMISSION_FOR_COMMAND( "Entschuldigung, Sie sind nicht berechtigt, diesen Befehl zu verwenden." ),
    ACCOUNT_HAS( "$1 hat $2" ),
    YOU_HAVE( "Sie haben $1" ),
    HELP( "MWSKonto Help" ),
    HELP_ARGUMENTS( "$1 erforderlich, $2 optional" ),
    RICH( "Reichen Liste" ),
    STARTING_UUID_CONVERSION( "Starten der UUID-Konvertierung" ),
    UUID_CONVERSION_FAILED( "Die UUID-Konvertierung ist fehlgeschlagen und MWSKonto wurde deaktiviert!" ),
    UUID_CONVERSION_SUCCEEDED( "UUID-Konvertierung war erfolgreich!" ),
    CONFIG_RELOADED( "Die Konfiguration wurde neu geladen." ),
    NOT_ENOUGH_konto( "Du hast nicht genug Geld." ),
    ACCOUNT_DOES_NOT_EXIST( "Entschuldigung, dieses Konto existiert nicht." ),
    YOUR_ACCOUNT_DOES_NOT_EXIST( "Sie haben kein Konto." ),
    ACCOUNT_EXISTS( "Dieser Account existiert bereits." ),
    ACCOUNT_CREATED( "Ein Account für $1 wurde erstellt." ),
    ACCOUNT_REMOVED( "Ein Account für $1 wurde entfernt." ),
    KONTO_RECEIVE( "Du hast $1 von $2 erhalten." ),
    KONTO_SENT( "Sie haben $1 an $2 gesendet" ),
    MAX_BALANCE_REACHED( "$1 hat das maximale Guthaben erreicht." ),
    PLAYER_SET_konto( "Sie haben das Guthaben von $1 auf $2 festgelegt." ),
    PLAYER_GRANT_konto( "Sie haben $1 bis $2 verschenkt." ),
    PLAYER_GRANTED_konto( "$2 schenkt Ihnen $1." ),
    PLAYER_DEDUCT_konto( "Sie haben $1 von $2 abgezogen." ),
    PLAYER_DEDUCTED_konto( "$2 abgezogen $1 von Ihrem Konto." ),
    ACCOUNT_CREATED_GRANT( "Erstellte ein Konto für $1 und gibt ihm $2" ),
    NO_ACCOUNTS_EXIST( "Es sind keine Konten vorhanden." ),
    NAME_TOO_LONG( "Sorry, dieser Name ist zu lang." ),
    ACCOUNT_CLEANED( "Alle Konten mit dem Standardguthaben wurden entfernt." ),
    TRY_COMMAND( "Versuch $1" ),
	COMMAND_IBANK( " /itembank zeigt die ItemBank an"),
	COMMAND_VIBANK( " /vipitembank zeigt die VipItemBank an"),
    PRIMARY_COLOR( ChatColor.GOLD.toString() ),
    SECONDARY_COLOR( ChatColor.AQUA.toString() ),
    TERTIARY_COLOR( ChatColor.GREEN.toString() ),
    ARGUMENT_COLOR( ChatColor.YELLOW.toString() ),
    COMMAND_BALANCE( "Zeigt das Guthaben an", true ),
    COMMAND_SEND( "Sendet einem anderen Spieler Geld", true ),
    COMMAND_TOP( "Liste die Top 5 der reichsten Spieler", true ),
    COMMAND_HELP( "Gives you help", true ),
    COMMAND_CREATE( "Erstellt ein Konto", true ),
    COMMAND_REMOVE( "Entfernt ein Konto", true ),
    COMMAND_SET( "Legen Sie das Guthaben eines Spielers fest", true ),
    COMMAND_GRANT( "Gibt einem Spieler Geld", true ),
    COMMAND_DEDUCT( "Nimmt Geld von einem Spieler", true ),
    COMMAND_CLEAN( "Bereinigt die Konten mit dem Standardguthaben", true ),
    COMMAND_RELOAD( "Konfiguration neu laden", true );

	

    private static konto plugin;
   
    private final String defaultMessage;

    private final boolean categorized;

    private String message;

    private expression( String defaultMessage )
    {
        this( defaultMessage, true );
    }

    private expression( String defaultMessage, boolean categorized )
    {
        this.defaultMessage = defaultMessage;

        this.categorized = categorized;

        message = defaultMessage + "";
    }

    public static void init( konto instance )
    {
        plugin = instance;
    }

    private String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public void reset()
    {
        message = defaultMessage + "";
    }

    public String getConfigName()
    {
        String name = name();

        if( categorized )
        {
            name = name.replaceFirst( "_", "." );
        }

        return name.toLowerCase();
    }

    public String parse( String... params )
    {
        String parsedMessage = getMessage();

        if( params != null )
        {
            for( int i = 0; i < params.length; i++ )
            {
                parsedMessage = parsedMessage.replace( "$" + ( i + 1 ), params[i] );
            }
        }

        return parsedMessage;
    }

    public String parseWithoutSpaces( String... params )
    {
        return parse( params ).replace( " ", "" );
    }

    private String parseWithPrefix( String... params )
    {
        return plugin.getMessagePrefix().replace( "$1", plugin.getConfig().getString( "prefix" ) ) + parse( params );
    }

    public void send( CommandSender sender, String... params )
    {
        sender.sendMessage( parse( params ) );
    }

    public void sendWithPrefix( CommandSender sender, String... params )
    {
        sender.sendMessage( parseWithPrefix( params ) );
    }
    public class expressions
    {	einstellungen settings = einstellungen.getInstance();
	  FileConfiguration data;
	  FileConfiguration vdata;	  
	  FileConfiguration ldata;	  
    konto plugin;}
    public void expressions( konto instance)
    {
    	
      plugin = instance;
    }  
}