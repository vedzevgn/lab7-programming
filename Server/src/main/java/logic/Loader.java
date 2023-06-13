package logic;

import data.Parser;
import data.collectionTools.DBCollectionLoader;
import logic.database.DBConnection;
import org.xml.sax.SAXException;
import parameters.MusicBand;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *A class containing methods for loading a collection from a file and initializing it.
 */

public class Loader {

    public static ArrayList<MusicBand> collection = new ArrayList<>();
    DBConnection connection;
    public static String dataPath = new String();
    public Loader(String path){
        dataPath = path;
    }

    public Loader(DBConnection connection){
        this.connection = connection;
    }

    public ArrayList<MusicBand> loadCollectionFromFile(){
        try {
            collection = Parser.deserialize(dataPath);
            System.out.println("Загружена коллекция: " + collection.getClass().getName() + ". Количество элементов: " + collection.size() + ".");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.print("Ошибка парсера. Проерьте исходный файл: " + e.getMessage() + "\n");
        }
        return collection;
    }

    public ArrayList<MusicBand> loadCollectionFromDB() {
        DBCollectionLoader dbLoader = new DBCollectionLoader(connection);
        try {
            dbLoader.checkTablesExists(connection.getDBConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        collection = dbLoader.loadCollection(connection);
        System.out.println("Загружена коллекция: " + collection.getClass().getName() + ". Количество элементов: " + collection.size() + ".");
        return collection;
    }
}
