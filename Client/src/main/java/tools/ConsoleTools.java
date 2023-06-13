package tools;

import connection.Connection;
import exceptions.InvalidArgsException;
import exceptions.InvalidResponseException;
import requests.RequestBuilder;
import responces.CommandType;
import responces.ResponseHandler;
import responces.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleTools {

    public static boolean isScriptRunning = false;
    public static void setScriptRunning(boolean scriptRunning) {
        isScriptRunning = scriptRunning;
    }

    private static final Map<String, Integer> intoFiles = new HashMap<>();

    static Map<String, Object[]> commands;
    static Connection manager;

    static CommandChecker checker = new CommandChecker();
    ResponseHandler parser = new ResponseHandler();

    static RequestBuilder builder = new RequestBuilder();

    public static boolean toStop = false;

    public static boolean toStop() {
        return toStop;
    }

    public static void setStop(boolean stop) {
        toStop = stop;
    }

    public static String username;
    public static String password;

    public ConsoleTools(Map<String, Object[]> commands, Connection manager, String username, String password){
        this.commands = commands;
        this.manager = manager;
        this.username = username;
        this.password = password;
    }
    public static void executeScript(Connection connection, String[] arguments) throws IOException {
        String[] args = {"file_name"};
        isScriptRunning = true;
        try {
            if (checker.isCorrectArgs(args, arguments)) {
                String script_path = arguments[0];
                File script_file = new File(script_path);
                String commandName = "";
                Integer count = 0;
                boolean makingArgs = false;
                try {
                    Scanner sc = new Scanner(script_file);
                    System.out.println("Выполнение скрипта...\n");
                    count++;
                    if(count > 10) {
                        toStop = true;
                        System.out.println("Достигнут максимальный уровень рекурсии.");
                        return;
                    }
                    if (sc.hasNext()) {
                        do {
                            String line = sc.nextLine();

                            if(!makingArgs){
                                commandName = line;
                            }

                            if(isScriptRunning && commands.containsKey(commandFromLine(line))) {
                                int argLines = 0;
                                argLines = (Integer) commands.get(commandFromLine(line))[2];
                                String[] scrArgs = new String[argLines];
                                if (checker.isObjectRequired(commands, commandFromLine(line)) || checker.isPostObjectRequired(commands, commandFromLine(line))) {
                                    line = sc.nextLine();
                                    makingArgs = true;
                                    for (int i = 0; i <= argLines - 1; i++) {
                                        scrArgs[i] = line;
                                        if(i != argLines-1)
                                            line = sc.nextLine();
                                        makingArgs = false;
                                    }
                                    CollectionTools tools = new CollectionTools();
                                    manager.send(builder.buildRequest(commandName, scrArgs, tools.createBand(scrArgs), username, password));
                                    Response response = (Response) manager.receive();
                                    System.out.println(response.getText());
                                    commandName = "";
                                } else {
                                    if(toStop()){
                                        return;
                                    }
                                    manager.send(builder.buildRequest(line, argsFromLine(line), null, username, password));
                                    if(sc.hasNext() && !toStop()) {
                                        Response response = (Response) manager.receive();
                                        System.out.println(response.getText());
                                    }
                                }
                            }
                        } while (sc.hasNext() && !toStop());
                        System.out.println("Выполнение скрипта завершено.");
                    } else {
                        System.out.println("Скрипт не содержит команд.");
                    }
                    isScriptRunning = false;
                } catch (FileNotFoundException | ClassNotFoundException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        } catch (InvalidArgsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void readyForExit(Response response){
        try {
            if(Objects.equals(parser.getText(response), "exit")){
                System.exit(1);
            }
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        }
    }

    //public boolean waitForReady(responces.Response response){
        //if(parser.getText(response) != )
    //}

    public static String commandFromLine(String line){
        if(line == null){
            System.exit(0);
        }
        String commandWord = line.toLowerCase().split(" ")[0];
        return commandWord;
    }

    public static String[] argsFromLine(String line){
        String[] arguments = new String[line.split(" ").length - 1];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = line.split(" ")[i + 1];
        }
        return arguments;
    }
}
