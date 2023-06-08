package data;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import parameters.Coordinates;
import parameters.MusicBand;
import parameters.MusicGenre;
import parameters.Studio;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Long.parseLong;

/**
 * This class makes conversion between MusicBand instances and XML structure.
 */

public class Parser {

    public static ArrayList<MusicBand> list = new ArrayList<>();

    public static ArrayList<Integer> IDs = new ArrayList<>();

    /**
     * Static method used for conversation XML structure into MusicBand instances.
     *
     * @param input - path of XML document
     */
    public static ArrayList<MusicBand> deserialize(String input) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        InputSource iSource = new InputSource();
        InputStream iStream = new BufferedInputStream(new FileInputStream(input));
        iSource.setByteStream(iStream);

        XMLHandler handler = new XMLHandler();
        parser.parse(iSource, handler);
        return list;
    }

    private static class XMLHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            try {
                if (qName.equals("MusicBand")) {
                    MusicBand band = new MusicBand();
                    Coordinates coordinates = new Coordinates();
                    Studio studio = new Studio();
                    band.setId(list.size());
                    IDs.add(Integer.parseInt(attributes.getValue("id")));
                    band.setName(attributes.getValue("name"));
                    if(checkIdDublicates(IDs)){
                        System.out.println("Ошибка. Дубликат значения ID у элемента с именем " + band.getName() + ", проверьте файл коллекции.");
                        System.exit(1);
                    }
                    coordinates.setX(Double.parseDouble(attributes.getValue("x")));
                    coordinates.setY(Float.parseFloat(attributes.getValue("y")));
                    band.setCoordinates(coordinates);
                    studio.setName(attributes.getValue("studio"));
                    band.setStudio(studio);
                    band.setGenre(MusicGenre.valueOf(attributes.getValue("genre")));
                    band.setNOP(parseLong(attributes.getValue("number_of_participants")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-d");
                    band.setCreationDate(LocalDate.parse(attributes.getValue("date"), formatter));
                    list.add(band);
                }

            } catch (NumberFormatException e){
                System.out.println("Ошибка парсера. Проерьте исходный файл: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    public static boolean checkIdDublicates(ArrayList<Integer> IDs){
        Set<Integer> setOfIDs = new HashSet<Integer>(IDs);
        if(setOfIDs.size() < IDs.size()){
            return true;
        } else {
            return false;
        }
    }
}