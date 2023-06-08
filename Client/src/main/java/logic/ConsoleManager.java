package logic;

import connection.Connection;
import exceptions.InvalidResponseException;
import requests.RequestBuilder;
import responces.Response;
import responces.ResponseHandler;
import tools.CollectionTools;
import tools.CommandChecker;
import tools.ConsoleTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class ConsoleManager {

    public boolean interactive = false;
    public RequestBuilder builder = new RequestBuilder();
    CollectionTools collectionTools = new CollectionTools();
    ConsoleTools consoleTools;

    CommandChecker checker = new CommandChecker();

    AuthorizationManager authorizationManager;

    private static final Map<String, Integer> intoFiles = new HashMap<>();

    public boolean skipReceive = false;

    private static final Logger logger = LogManager.getLogger();

    public Map<String, Object[]> commands;

    public static String username = "";
    public static String password = "";
    public static boolean loggedIn = false;

    public ConsoleManager(Map<String, Object[]> commands, ConsoleTools consoleTools, String username, String password){
        this.commands = commands;
        this.consoleTools = consoleTools;
        this.username = username;
        this.password = password;
    }

    public void output(Connection connection, BufferedReader scanner) throws IOException, ClassNotFoundException, InvalidResponseException {
        if(!interactive) {
            System.out.print(">>> ");
        }

        ResponseHandler parser = new ResponseHandler();
        Response serverResponse;

        if(sendRequest(connection, scanner) && !skipReceive) {
            serverResponse = (Response) connection.receive();
            logger.info("Получен ответ.");
            try {
                System.out.println(parser.getText(serverResponse));

                if (parser.getInteractive(serverResponse)) {
                    interactive = true;
                }
                if (!parser.getInteractive(serverResponse)) {
                    interactive = false;
                }
            } catch (InvalidResponseException ire){
                System.out.println(ire.getMessage());
                System.exit(0);
            }

        }
        skipReceive = false;

    }


    public boolean sendRequest(Connection connection, BufferedReader scanner) throws IOException, ClassNotFoundException, InvalidResponseException {
        String line = scanner.readLine();
        authorizationManager = new AuthorizationManager(connection);
        if(checker.isCorrect(commands, consoleTools.commandFromLine(line), consoleTools.argsFromLine(line))) {
            if (checker.isExit(commands, consoleTools.commandFromLine(line))) {
                connection.send(builder.buildRequest(line, null, null, username, password));
                System.out.println(((Response) connection.receive()).getText());
                consoleTools.readyForExit((Response) connection.receive());
                System.exit(0);
            }
            if (checker.isObjectRequired(commands, consoleTools.commandFromLine(line))) {
                connection.send(builder.buildRequest(line, null, collectionTools.createBand(null), username, password));
            } else if (checker.isPostObjectRequired(commands, consoleTools.commandFromLine(line))) {
                connection.send(builder.buildRequest(line, null, null, username, password));
                Response response = (Response) connection.receive();
                if (!response.isReady()) {
                    connection.send(builder.buildRequest(line, null, collectionTools.createBand(null), username, password));
                    System.out.println(((Response) connection.receive()).getText());
                    skipReceive = true;
                } else {
                    System.out.println(response.getText());
                    skipReceive = true;
                }
            } else if (checker.isExecute(commands, consoleTools.commandFromLine(line))) {
                String[] args = new String[1];
                args[0] = line.split(" ")[1];
                consoleTools.executeScript(connection, args);
            } else if (checker.isAuthorization(commands, consoleTools.commandFromLine(line))){
                if (checker.isLogin(commands, consoleTools.commandFromLine(line))) {
                    if (!loggedIn) {
                        String[] dataForAuth = new String[2];
                        dataForAuth = authorizationManager.login();
                        username = dataForAuth[0];
                        password = dataForAuth[1];
                    } else {
                        System.out.println("Вход уже выполнен.");
                        skipReceive = true;
                    }
                } else {
                    if (!loggedIn) {
                        String[] dataForAuth = new String[3];
                        dataForAuth = authorizationManager.signup();
                        username = dataForAuth[0];
                        password = dataForAuth[1];
                    } else {
                        System.out.println("Вход уже выполнен.");
                        skipReceive = true;
                    }
                }
            } else if (checker.isLogout(commands, consoleTools.commandFromLine(line))){
                loggedIn = false;
                username = "";
                password = "";
                System.out.println("Вы вышли из системы.");
                skipReceive = true;
            } else {
                connection.send(builder.buildRequest(line, null, null, username, password));
            }
            return true;
        } else {
            return false;
        }
    }

}
