import commands.Invoker;
import commands.Receiver;
import connection.DatagramConnection;
import exceptions.InvalidArgsException;
import logic.database.DBSettings;
import logic.database.DBConnection;
import requests.RequestHandler;
import responces.ResponseBuilder;
import requests.Request;
import logic.serverLogic.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A class containing methods for program and user interaction.
 */

public class Server {
    public String dataPath;
    public Receiver receiver;
    public static DatagramConnection manager;
    public Invoker invoker;
    public ResponseBuilder builder;
    public CommandChecker checker = new CommandChecker();
    public boolean executing = false;
    public static boolean running = true;
    String[] array;
    static byte[] buffer = new byte[32];
    static boolean checkSkip;

    private static final Logger logger = LogManager.getLogger();

    String DBUsername;
    String DBPassword;
    String URLTail;

    public Server(String[] args) throws IOException, InvalidArgsException {
        if(args.length != 3){
            logger.error("Проверьте количество аргументов.");
            System.exit(1);
        } else {
            this.dataPath = args[0];
        }
        DBUsername = args[0];
        DBPassword = args[1];
        URLTail = args[2];
    }

    public static void setCheckSkip(boolean checkSkip) {
        Server.checkSkip = checkSkip;
    }

    public void start() throws IOException, InvalidArgsException, ClassNotFoundException, SQLException, ExecutionException, InterruptedException {
        logger.info("Запуск сервера.");
        //System.out.println("𝘃𝗲𝗱𝘇𝗲𝘃𝗴𝗻\n𝘀𝗲𝗿𝘃𝗲𝗿 (𝗠𝘂𝘀𝗶𝗰𝗕𝗮𝗻𝗱 𝗰𝗼𝗹𝗹𝗲𝗰𝘁𝗶𝗼𝗻)\n");

        DBSettings settings = new DBSettings(DBUsername, DBPassword, URLTail);

        DBConnection dbConnection;

        try {
            dbConnection = new DBConnection(settings.getURL(), settings.getUserName(), settings.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        DatagramConnection connectionManager = new DatagramConnection();
        RequestHandler handler = new RequestHandler();
        builder = new ResponseBuilder();

        try {
            manager = new DatagramConnection(16015, true);
            receiver = new Receiver(dataPath, manager, builder, dbConnection);
            invoker = new Invoker(receiver, manager, builder);
            logger.info("Invoker и Receiver запущены.");
        } catch (SocketException e) {
            logger.error("Адрес уже используется.");
            e.printStackTrace();
            running = false;
            System.exit(1);
        } catch (UnknownHostException e) {
            running = false;
            logger.error("Неизвестный хост.");
            System.exit(1);
        }

        Callable<Request> callableRequestGetter = () -> {
            return (Request) manager.receive();
        };

        new Thread(() -> {
            Request request;

            while(running){
                try {
                    request = callableRequestGetter.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                checkSkip = false;
            //request = (Request) manager.receive();

            String line = handler.getText(request);

            if(Objects.equals(line, "ready")){
                //manager.setPort(manager.getPort());
                CommandsSender commandsSender = new CommandsSender(manager);
                commandsSender.sendCommands();
                logger.info("Список команд отправлен.");
                checkSkip = true;
            }

            Object object = handler.getObject(request);

            String username = handler.getUsername(request);
            String password = handler.getPassword(request);


            if(!checkSkip) {
                if (!checker.isExecute(line) && !executing) {
                    String thisLine = line;
                        logger.info("Получена команда: " + thisLine + ".");
                        try {
                            invoker.runCommand(thisLine, dataPath, false, array, object, username, password);
                        } catch (InvalidArgsException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                } else {
                    request = (Request) manager.receive();
                    line = handler.getText(request);
                    String[] arguments = handler.getArgs(request);
                    executing = true;
                    try {
                        invoker.runCommand(line, dataPath, true, arguments, object, username, password);
                    } catch (InvalidArgsException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            checkSkip = false;
        }
        }).start();
    }
}


