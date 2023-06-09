import connection.ChannelConnection;
import connection.Connection;
import exceptions.InvalidArgsException;
import exceptions.InvalidResponseException;
import logic.ConsoleManager;
import logic.*;
import connection.DatagramConnection;
import requests.RequestBuilder;
import tools.ConsoleTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static logic.ConsoleManager.password;
import static logic.ConsoleManager.username;

/**
 * A class containing methods for program and user interaction.
 */

public class Client {
    public String dataPath;

    DatagramSocket socket;

    DatagramConnection manager = new DatagramConnection();
    private Connection connection;

    private static boolean running = true;

    public boolean interactive = false;

    Map commands;

    String[] array;

    BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
    AuthorizationManager authManager;

    private static final Logger logger = LogManager.getLogger();

    public Client(String[] args) throws IOException, InvalidArgsException {}

    public void start() throws IOException, InvalidArgsException, ClassNotFoundException, InvalidResponseException {
        logger.info("Клиент запущен.");
        System.out.println("𝘃𝗲𝗱𝘇𝗲𝘃𝗴𝗻\n𝗰𝗹𝗶𝗲𝗻𝘁 (𝗠𝘂𝘀𝗶𝗰𝗕𝗮𝗻𝗱 𝗰𝗼𝗹𝗹𝗲𝗰𝘁𝗶𝗼𝗻)\n");
        connection = new ChannelConnection("localhost", 16015);

        //authManager.login();

        connection.send(new RequestBuilder().buildRequest("ready", null, null, username, password));
        commands = (Map) connection.receive();
        if(commands == null){
            System.out.println("Cписок команд не получен.");
            logger.error("Cписок команд не получен.");
            System.exit(1);
        }
        logger.info("Список команд получен.");

        ConsoleTools consoleTools = new ConsoleTools(commands, connection, username, password);
        ConsoleManager consoleManager = new ConsoleManager(commands, consoleTools, username, password);

        while (true) {
            consoleManager.output(connection, scanner);
        }
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public Map<String, Object[]> getCommands(){
        return commands;
    }
}


