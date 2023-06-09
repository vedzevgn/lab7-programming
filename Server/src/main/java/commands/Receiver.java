package commands;

import authorization.Authorization;
import commands.consoleCommands.*;

import connection.Connection;
import data.collectionTools.DBCollectionManager;
import exceptions.InvalidArgsException;
import logic.Loader;
import logic.database.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.Request;
import requests.RequestHandler;
import responces.ResponseBuilder;
import parameters.MusicBand;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

/**
 * This class is necessary for reading and executing commands
 */

public class Receiver {
    public static String path = "";
    public static ArrayList<MusicBand> list;

    static ResponseBuilder builder;

    static Connection manager;

    static RequestHandler parser = new RequestHandler();

    private static final Logger logger = LogManager.getLogger();

    public DBConnection dbconnection;

    DBCollectionManager dbManager;

    Authorization authorization;

    ReentrantLock reentrantLockOnWrite = new ReentrantLock();
    ReentrantLock reentrantLockOnRead = new ReentrantLock();

    public static java.sql.Connection SQLConnection;

    public Receiver(String filepath, Connection iomanager, ResponseBuilder respBuilder, DBConnection dbconnection) throws SQLException, FileNotFoundException {
        path = filepath;
        manager = iomanager;
        builder = respBuilder;
        this.dbconnection = dbconnection;
        this.SQLConnection = dbconnection.getDBConnection();
        list = new Loader(dbconnection).loadCollectionFromDB();
        dbManager = new DBCollectionManager(SQLConnection);
        authorization = new Authorization(SQLConnection);
        logger.info("Receiver инициализорован.");
    }


    /**
     *This class has a static method runCommand which reads commands
     *and executes them.
     */


    public boolean loginCommand(String[] arguments, String path, boolean isScript, Object object) throws SQLException, IOException {
        if(authorization.logIn(arguments[0], arguments[1])) {
            manager.send(builder.createResponse(arguments[0], false, true));
            manager.send(builder.createResponse("Здравствуйте, " + authorization.getName(arguments[0], arguments[1]) + "!\nВведите help для просмотра доступных команд.", false, true));
        } else {
            manager.send(builder.createResponse("failed", false, true));
            manager.send(builder.createResponse("Неверное имя пользователя или пароль.", false, true));
        }
        return true;
    }

    public boolean signUpCommand(String[] arguments, String path, boolean isScript, Object object) throws SQLException, IOException {
        if(authorization.signUp(arguments[0], arguments[1], arguments[2])) {
            manager.send(builder.createResponse(arguments[0], false, true));
            manager.send(builder.createResponse("Добро пожаловать, " + authorization.getName(arguments[1], arguments[2]) + "!\nВведите help для просмотра доступных команд.", false, true));
        } else {
            manager.send(builder.createResponse("failed", false, true));
            manager.send(builder.createResponse("Неверное имя пользователя или пароль.", false, true));
        }
        return true;
    }

    Integer lastID = 0;
    public ArrayList<MusicBand> addCommand(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws IOException, ClassNotFoundException, SQLException {
        MusicBand newBand = (MusicBand) object;
        //System.out.println("Принят объект: " + newBand.getName());
        logger.info("Принят объект: " + newBand.getName() + ".");
        //System.out.println(newBand);

        reentrantLockOnWrite.lock();
        System.out.println("ID" + ID);
        lastID = dbManager.addBand(newBand, ID);
        newBand.setId(lastID);
        newBand.setUserID(ID);
        reentrantLockOnWrite.unlock();

        list.add(newBand);
        //System.out.println("\nДобавлен новый объект: " + newBand.getName() + " (ID: " + newBand.getId() + ")");
        manager.send(builder.createResponse("\nДобавлен новый объект: " + newBand.getName() + " (ID: " + newBand.getId() + ")", false, true));
        return list;
    }

    public void averageCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];

        int NOPsum = 0;
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                for (MusicBand musicBand : list) {
                    NOPsum += musicBand.getNOP();
                }
                //System.out.println("Среднее кол-во участиников: " + (NOPsum / list.size()));
                manager.send(builder.createResponse("Среднее кол-во участиников: " + (NOPsum / list.size()), false, true));
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }

    public void clearCommand(String[] arguments, String path, boolean isScript, Object object, Integer ID) {
        String[] args = new String[0];

        try {
            if (Command.isCorrectArgs(args, arguments)) {
                ClearOperations operations = new ClearOperations();
                manager.send(builder.createResponse("Вы уверены, что хотите очистить коллекцию (будут удалены только ваши объекты)? Данное действие необратимо. (y/n)", true, true));
                if (operations.confirm(parser.getText((Request) manager.receive()))) {
                    reentrantLockOnWrite.lock();
                    dbManager.removeBandsOfUser(ID);
                    for(int i = 0; i < list.size(); i++){
                        if(Objects.equals(list.get(i).getUserID(), ID)){
                            list.remove(i);
                        }
                    }
                    reentrantLockOnWrite.unlock();
                }
            }
        } catch (InvalidArgsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ClearOperations {
        public boolean confirm(String input) {
            boolean start = false;
            if (Objects.equals(input, "y")) {
                //System.out.println("Коллекция очищена.");
                try {
                    manager.send(builder.createResponse("Коллекция очищена.", false, true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                start = true;
            } else if (Objects.equals(input, "n")) {
                //System.out.println("Операция отменена.");
                try {
                    manager.send(builder.createResponse("Операция отменена.", false, true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return start;
        }
    }

    public void executeCommand(String[] arguments, String path, boolean isScript, Object object) throws IOException {
        //System.out.println("execute");
    }

    public void exitCommand(String[] arguments, String path, boolean isScript, Object object) throws IOException {
        //saveCommand(arguments, path, isScript, object);
        manager.send(builder.createResponse("exit", false, true));
    }

    public void filterCommand(String[] arguments, String path, boolean isScript, Object object) {
        try {
            if (Command.isCorrectArgs(new String[1], arguments)) {
                List<MusicBand> bands = list.stream()
                        .filter(band -> band.getGenre().toString().equals(arguments[0].toUpperCase()))
                        .collect(Collectors.toList());
                if (bands.isEmpty()) {
                    //System.out.println("Не найдено элементов с заданным критерием.");
                    manager.send(builder.createResponse("Не найдено элементов с заданным критерием.", false, true));
                } else {
                    String fullLine = bands.stream()
                            .map(band -> "ID: " + band.getId() + "\nИмя: " + band.getName() + "\nЖанр: " + band.getGenre() + "\nX: " + band.getCoordinates().getX() + "\nY: " + band.getCoordinates().getY() + "\nСтудия: " + band.getStudio().getName() + "\nКол-во участников: " + band.getNOP() + "\n\n")
                            .collect(Collectors.joining());
                    manager.send(builder.createResponse(fullLine, false, true));
                }
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void helpCommand(String[] arguments, String path, boolean isScript, Object object, Integer ID) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                if (ID != 0) {
                    manager.send(builder.createResponse("""
                            help : вывести справку по доступным командам
                            info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
                            show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
                            add {element} : добавить новый элемент в коллекцию
                            update id {element} : обновить значение элемента коллекции, id которого равен заданному
                            remove_by_id id : удалить элемент из коллекции по его id
                            clear : очистить коллекцию
                            save : сохранить коллекцию в файл
                            execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
                            exit : завершить программу (без сохранения в файл)
                            shuffle : перемешать элементы коллекции в случайном порядке
                            reorder : отсортировать коллекцию в порядке, обратном нынешнему
                            history : вывести последние 15 команд (без их аргументов)
                            sort : отсортировать коллекцию в естественном порядке
                            average_of_number_of_participants : вывести среднее значение поля numberOfParticipants для всех элементов коллекции
                            filter_by_genre genre : вывести элементы, значение поля genre которых равно заданному
                            print_field_descending_genre : вывести значения поля genre всех элементов в порядке убывания""", false, true));
                } else {
                    manager.send(builder.createResponse("""
                            help : вывести справку по доступным командам
                            exit : завершить программу
                            login : войти в систему
                            sign_up : зарегистрироваться в системе
                            """, false, true));
                }
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void historyCommand(String[] arguments, String path, boolean isScript, Object object) throws IOException {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                String full = "";
                ArrayList<String> history = Invoker.getLastCommands();
                for (int i = history.size() - 1; i >= 0; i--) {
                    System.out.println(history.get(i));
                    full += history.get(i) + "\n";
                }
                manager.send(builder.createResponse(full, false, true));
            }
        } catch (InvalidArgsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void infoCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                Path filepath = Paths.get(path);
                BasicFileAttributes attr;
                FileTime fileTime;
                String fullLine = "";

                //System.out.println("Тип коллекции: " + list.getClass().getName());
                fullLine += "Тип коллекции: " + list.getClass().getName() + "\n";
                //System.out.println("Размер коллекции: " + list.size());
                fullLine += "Размер коллекции: " + list.size() + "\n";
                attr = Files.readAttributes(filepath, BasicFileAttributes.class);
                //System.out.println("Дата инициализации: " + attr.creationTime());
                fullLine += "Дата инициализации: " + attr.creationTime() + "\n";
                //System.out.println("Дата изменения: " + attr.lastModifiedTime());
                fullLine += "Дата изменения: " + attr.lastModifiedTime() + "\n";
                fullLine += list.stream()
                        .map(band -> "\t- " + band.getName() + "\n")
                        .collect(Collectors.joining());
                manager.send(builder.createResponse(fullLine, false, true));
            }
        } catch (InvalidArgsException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void printGenreCommand(String[] arguments, String path, boolean isScript, Object object) {
        try {
            if (Command.isCorrectArgs(new String[0], arguments)) {
                if (list.isEmpty()) {
                    //System.out.println("Коллекция пуста.");
                    manager.send(builder.createResponse("Коллекция пуста.", false, true));
                } else {
                    String fullLine = list.stream()
                            .sorted(Comparator.comparing(MusicBand::getId))
                            .map(musicBand -> "ID: " + musicBand.getId() + "\nИмя: " + musicBand.getName() + "\nЖанр: " + musicBand.getGenre() + "\n")
                            .collect(Collectors.joining());
                    //System.out.println(fullLine);
                    manager.send(builder.createResponse(fullLine, false, true));
                }
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void removeByIdCommand(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws ClassNotFoundException {
        try {
            if (Command.isCorrectArgs(new String[1], arguments)) {
                int id = Integer.parseInt(arguments[0]);
                Optional<MusicBand> band = list.stream()
                        .filter(b -> b.getId() == id)
                        .findFirst();
                if (band.isPresent()) {
                    if(Objects.equals(ID, band.get().getUserID())) {
                        manager.send(builder.createResponse("Вы уверены, что хотите удалить элемент? Данное действие необратимо. (y/n)", true, true));
                        Request request = (Request) manager.receive();
                        if (new RemoveOperations().confirm(parser.getText(request))) {
                            reentrantLockOnWrite.lock();
                            dbManager.removeBand(band.get().getId());
                            reentrantLockOnWrite.unlock();
                            list.remove(band.get());
                            manager.send(builder.createResponse("Из коллекции удалён объект с ID: " + id, false, true));
                        }
                    } else {
                        manager.send(builder.createResponse("Вы не можете модифицировать объекты, созданные другими пользователями.", false, true));
                    }
                } else {
                    manager.send(builder.createResponse("Максимальный ID элемента: " + dbManager.getMaxID(), false, true));
                }
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class RemoveOperations {
        public boolean confirm(String input) {
            boolean start = false;
            if (Objects.equals(input, "y")) {
                start = true;
            } else if (Objects.equals(input, "n")) {
                //System.out.println("Операция отменена.");
                try {
                    manager.send(builder.createResponse("Операция отменена.", false, true));
                } catch (IOException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
            return start;
        }
    }

    public void reorderCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                Collections.reverse(list);
                //System.out.println("Коллекция отсортирована в обратном порядке.");
                manager.send(builder.createResponse("Коллекция отсортирована в обратном порядке.", false, true));
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }

    public void saveCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MusicBands>\n";
                data += list.stream()
                        .map(band -> "\t<MusicBand " + "id=\"" + band.getId() + "\" name=\"" + band.getName().replace("\"","&quot;") + "\" genre=\"" + band.getGenre().toString().replace("\"","&quot;") + "\" x=\"" + band.getCoordinates().getX().toString().replace("\"","&quot;") + "\" y=\"" + band.getCoordinates().getY().toString().replace("\"","&quot;") + "\" studio=\"" + band.getStudio().getName().replace("\"","&quot;") + "\" number_of_participants=\"" + band.getNOP() + "\" date=\"" + band.getCreationDate() + "\" />\n")
                        .collect(Collectors.joining());
                data += "</MusicBands>\n";

                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path))) {
                    output.write(data.getBytes());
                    //System.out.println("Коллекция сохранена в файл: " + path);
                    manager.send(builder.createResponse("Коллекция сохранена. ", false, true));
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }

                data = "";
                try {
                    FileOutputStream file = new FileOutputStream(path);

                    BufferedOutputStream output = new BufferedOutputStream(file);

                    byte[] array = data.getBytes();

                    data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                    array = data.getBytes();
                    output.write(array);
                    data = "<MusicBands>\n";
                    array = data.getBytes();
                    output.write(array);
                    for (MusicBand band : list) {
                        data = "\t<MusicBand " + "id=\"" + band.getId() + "\" name=\"" + band.getName().replace("\"","&quot;") + "\" genre=\"" + band.getGenre().toString().replace("\"","&quot;") + "\" x=\"" + band.getCoordinates().getX().toString().replace("\"","&quot;") + "\" y=\"" + band.getCoordinates().getY().toString().replace("\"","&quot;") + "\" studio=\"" + band.getStudio().getName().replace("\"","&quot;") + "\" number_of_participants=\"" + band.getNOP() + "\" date=\"" + band.getCreationDate() + "\" />\n";
                        array = data.getBytes();
                        output.write(array);
                    }
                    data = "</MusicBands>\n";
                    array = data.getBytes();
                    output.write(array);
                    System.out.println("Коллекция сохранена в файл: " + path);
                    output.close();
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }


            }
        } catch (InvalidArgsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void showCommand(String[] arguments, String path, boolean isScript, Object object) {
        try {
            if (Command.isCorrectArgs(new String[0], arguments)) {
                reentrantLockOnRead.lock();
                if (list.isEmpty()) {
                    //System.out.println("Коллекция пуста.");
                    manager.send(builder.createResponse("Коллекция пуста.", false, true));
                } else {
                    String fullList = list.stream()
                            .map(musicBand -> "ID: " + musicBand.getId() + "\nИмя: " + musicBand.getName() + "\nЖанр: " + musicBand.getGenre() + "\nX: " + musicBand.getCoordinates().getX() + "\nY: " + musicBand.getCoordinates().getY() + "\nСтудия: " + musicBand.getStudio().getName() + "\nКол-во участников: " + musicBand.getNOP() + "\nДата создания: " + musicBand.getCreationDate() + "\nID пользователя: " + musicBand.getUserID() + "\n\n")
                            .collect(Collectors.joining());
                    //System.out.println(fullList);
                    manager.send(builder.createResponse(fullList, false, true));
                }
                reentrantLockOnRead.unlock();
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void shuffleCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                Collections.shuffle(list);
                //System.out.println("Коллекция перемешана в случайном порядке.");
                manager.send(builder.createResponse("Коллекция перемешана в случайном порядке.", false, true));
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }

    public void sortCommand(String[] arguments, String path, boolean isScript, Object object) {
        String[] args = new String[0];
        try {
            if (Command.isCorrectArgs(args, arguments)) {
                reentrantLockOnRead.lock();
                for (int k = 0; k < list.size(); k++) {
                    if (k < list.size() - 1) {
                        if (list.get(k + 1).getId() < list.get(k).getId()) {
                            new SortOperations().sorting(list);
                        }
                    }
                }
                reentrantLockOnRead.unlock();
                //System.out.println("Коллекция отсортирована по ID.");
                manager.send(builder.createResponse("Коллекция отсортирована по ID.", false, true));
            }
        } catch (InvalidArgsException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }

    public static class SortOperations {
        public void sorting(ArrayList<MusicBand> list) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    if (list.get(i).getId() > list.get(i + 1).getId()) {
                        Collections.swap(list, i, i + 1);
                        sorting(list);
                    }
                }
            }
        }
    }
    /*public void updateByIdCommand(String[] arguments, String path, boolean isScript, Object object) throws IOException, ClassNotFoundException {
        if(Integer.parseInt(arguments[0]) > list.size()-1 || Integer.parseInt(arguments[0]) < 0){
            System.out.println("Максимальный ID: " + (list.size()-1) + ". Минимальный ID: 0.");
            try {
                manager.send(builder.createResponse("Максимальный ID: " + (list.size()-1) + ". Минимальный ID: 0.", false, true));
            } catch (IOException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        } else {
            manager.send(builder.createResponse("", true, false));
            int idToReplace = Integer.parseInt(arguments[0]);
            MusicBand newBand = (MusicBand) parser.getObject((Request) manager.receive());
            System.out.println("Принят объект: " + newBand.getName());
            //System.out.println(newBand);
            newBand.setId(list.get(idToReplace).getId());
            manager.send(builder.createResponse("Объект заменён на: " + newBand.getName() + " (ID: " + newBand.getId() + ")", false, true));
            list.set(idToReplace, newBand);
            System.out.println("Объект заменён на: " + newBand.getName() + " (ID: " + newBand.getId() + ")");
        }
    }*/

    public void updateByIdCommand(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws IOException, ClassNotFoundException, SQLException {
        int id = Integer.parseInt(arguments[0]);
        Optional<MusicBand> band = list.stream().filter(b -> b.getId() == id).findFirst();
        if (band.isPresent()) {
            if(Objects.equals(ID, band.get().getUserID())) {
                manager.send(builder.createResponse("", true, false));
                MusicBand newBand = (MusicBand) parser.getObject((Request) manager.receive());
                //System.out.println("Принят объект: " + newBand.getName());
                logger.info("Принят объект: " + newBand.getName() + ".");

                reentrantLockOnWrite.lock();
                if(dbManager.updateBand(newBand, id, ID)) {
                    newBand.setId(id);
                    list.set(list.indexOf(band.get()), newBand);
                    manager.send(builder.createResponse("Объект заменён на: " + newBand.getName() + " (ID: " + newBand.getId() + ")", false, true));
                } else {
                    manager.send(builder.createResponse("Объект с указанным ID не существует.", false, true));
                }
                reentrantLockOnWrite.unlock();

            } else {
                manager.send(builder.createResponse("Вы не можете модифицировать объекты, созданные другими пользователями.", false, true));
            }
            manager.send(builder.createResponse("", true, false));
            MusicBand newBand = (MusicBand) parser.getObject((Request) manager.receive());
            //System.out.println("Принят объект: " + newBand.getName());
            logger.info("Принят объект: " + newBand.getName() + ".");

            reentrantLockOnWrite.lock();
            if(dbManager.updateBand(newBand, id, ID)) {
                newBand.setId(id);
                list.set(list.indexOf(band.get()), newBand);
                manager.send(builder.createResponse("Объект заменён на: " + newBand.getName() + " (ID: " + newBand.getId() + ")", false, true));
            } else {
                manager.send(builder.createResponse("Объект с указанным ID не существует.", false, true));
            }
            reentrantLockOnWrite.unlock();

        } else {
            //System.out.println("Максимальный ID: " + (list.size()-1) + ". Минимальный ID: 0.");
            manager.send(builder.createResponse("Не существует элемента с заданным ID.", false, true));
        }
    }
}
