package commands;

import authorization.Authorization;
import commands.consoleCommands.*;

import connection.Connection;
import connection.DatagramConnection;
import exceptions.InvalidArgsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import responces.ResponseBuilder;

import parameters.MusicBand;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static commands.Receiver.SQLConnection;

/**
 * This class is necessary for reading and executing supported commands
 */

public class Invoker {
    public static final Map<String, Command> commands = new HashMap<>();
    private static final Map<String, Integer> intoFiles = new HashMap<>();

    public static Receiver receiver;


    public boolean isScriptRunning = false;

    static Command command;

    public static String[] arguments;

    public static String commandWord;
    public static Connection manager;
    public static ResponseBuilder builder;

    public static java.sql.Connection connection;

    private static final Logger logger = LogManager.getLogger();

    public Invoker(Receiver receiver, Connection manager, ResponseBuilder builder) {
        this.manager = manager;
        this.builder = builder;
        commands.put("help", new Help(receiver));
        commands.put("add", new Add(receiver));
        commands.put("show", new Show(receiver));
        commands.put("exit", new Exit(receiver));
        commands.put("clear", new Clear(receiver));
        commands.put("reorder", new Reorder(receiver));
        commands.put("shuffle", new Shuffle(receiver));
        commands.put("sort", new Sort(receiver));
        commands.put("history", new History(receiver));
        commands.put("remove_by_id", new RemoveById(receiver));
        commands.put("update", new Update(receiver));
        commands.put("execute_script", new Execute(receiver));
        commands.put("average_of_number_of_participants", new Average(receiver));
        commands.put("average_of_nop", new Average(receiver));
        commands.put("info", new Info(receiver));
        commands.put("filter_by_genre", new Filter(receiver));
        commands.put("print_field_descending_genre", new PrintGenre(receiver));
        commands.put("save", new Save(receiver));
        commands.put("помогите", new Help(receiver));
        commands.put("login", new LogIn(receiver));
        commands.put("sign_up", new SignUp(receiver));
        logger.info("Invoker инициализирован.");
    }

    static ArrayList<String> lastCommands = new ArrayList<String>();

    public static ArrayList<String> getLastCommands() {
        return lastCommands;
    }


    public static int inputsCount = 0;

    /*
     *This class has a static method push which reads commands
     *and executes them.
     */
    public static void push(String path, boolean isScriptRunning, Object object, String username, String password) throws InvalidArgsException, IOException, SQLException {
        logger.info("Проверка команды.");
        if(commands.containsKey(commandWord)) {
            if(lastCommands.size() == 15) {
                lastCommands.remove(0);
                lastCommands.add(commandWord);
            } else {
                lastCommands.add(commandWord);
            }
            command = commands.get(commandWord);

            if(command.isAuthorizationRequired()){
                if(new Authorization(SQLConnection).logIn(username, password)){
                    try {
                        logger.info("Запуск выполнения команды.");
                        command.execute(arguments, path, isScriptRunning, object, new Authorization(SQLConnection).getUserID(username));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    manager.send(builder.createResponse("Для использования данной команды необходима авторизация.\nВведите help для просмотра доступных команд.", false, true));
                }
            } else {
                try {
                    logger.info("Запуск выполнения команды.");
                    command.execute(arguments, path, isScriptRunning, object, new Authorization(SQLConnection).getUserID(username));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            //System.out.print("Неизвестная команда " + commandWord + ". Введите help для просмотра списка доступных команд.\n");
            logger.error("Неизвестная команда: " + commandWord + ".");
            manager.send(builder.createResponse("Неизвестная команда " + commandWord + ". Введите help для просмотра списка доступных команд.\n", false, true));
        }

    }

    public void runCommand(String line, String path, boolean isScriptRunning, String[] scrArguments, Object object, String username, String password) throws InvalidArgsException, IOException, SQLException {
        if (line.equals("")) return;
        commandWord = commandFromLine(line);
        arguments = argsFromLine(line);
        if(isScriptRunning){
            arguments = scrArguments;
        }

        push(path, isScriptRunning, object, username, password);
    }

    public static String commandFromLine(String line){
        String commandWord = line.toLowerCase().split(" ")[0];
        String[] arguments = new String[line.split(" ").length - 1];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = line.split(" ")[i + 1];
        }
        return commandWord;
    }

    public static String[] argsFromLine(String line){
        String[] arguments = new String[line.split(" ").length - 1];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = line.split(" ")[i + 1];
        }
        return arguments;
    }

    public static boolean isContains(String name){
        return commands.containsKey(name);
    }

    public static Command getCommand(String name){
        return commands.get(name);
    }

    public Map<String, Command> getCommands(){
        return commands;
    }
}
