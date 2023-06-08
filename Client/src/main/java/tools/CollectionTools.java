package tools;

import parameters.Coordinates;
import parameters.MusicBand;
import parameters.MusicGenre;
import parameters.Studio;
import validators.GenreValidator;
import validators.NOPValidator;
import validators.NameValidator;
import validators.coordinatesValidators.XCoordinateValidator;
import validators.coordinatesValidators.YCoordinateValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Long.parseLong;

public class CollectionTools {
    public MusicBand createBand(Object[] objects) throws IOException {
        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
        MusicBand newBand = new MusicBand();
        if(objects == null){
            String[] nullArray = new String[0];
            objects = nullArray;
        }
        if (objects.length == 0) {
            try {
                setName(newBand, sc, "");
                setGenre(newBand, sc, "");
                setX(newBand, sc, "");
                setY(newBand, sc, "");
                setStudio(newBand, sc, "");
                setNOP(newBand, sc, "");
                setDate(newBand, sc, "");

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Введён неправильный тип данных...");
            }
        } else {
            try {
                setName(newBand, sc, objects[0]);
                setGenre(newBand, sc, objects[1]);
                setX(newBand, sc, objects[2]);
                setY(newBand, sc, objects[3]);
                setStudio(newBand, sc, objects[4]);
                setNOP(newBand, sc, objects[5]);
                setDate(newBand, sc, null);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Введён неправильный тип данных...");
            }
        }
        return done(newBand);
    }

        Coordinates coordinates = new Coordinates();
        Studio studio = new Studio();
        String argument = "";

        public void setName(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            NameValidator nameValidator = new NameValidator();
            if (Objects.equals(scrArg, "")) {
                System.out.print("Введите значение поля Name: ");
                argument = readConsole(scanner);
                newBand.setName(argument);
                if (!nameValidator.validate(argument)) {
                    setName(newBand, scanner, scrArg);
                }
            } else {
                argument = (String) scrArg;
                newBand.setName(argument);
                if (!nameValidator.validate(argument)) {
                    argument = readConsole(scanner);
                    setName(newBand, scanner, scrArg);
                }
            }
        }

        public void setGenre(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            GenreValidator genreValidator = new GenreValidator();
            if (Objects.equals(scrArg, "")) {
                System.out.print("\nВведите значение поля Genre\n(PSYCHEDELIC_ROCK\nPSYCHEDELIC_CLOUD_RAP\nMATH_ROCK\nPOST_ROCK): ");
                argument = readConsole(scanner);
                System.out.println(argument);
                if (genreValidator.validate(argument.toUpperCase())) {
                    MusicGenre mg = MusicGenre.valueOf(argument.toUpperCase());
                    newBand.setGenre(mg);
                } else {
                    setGenre(newBand, scanner , scrArg);
                }
            } else {
                argument = (String) scrArg;
                if (genreValidator.validate(argument.toUpperCase())) {
                    MusicGenre mg = MusicGenre.valueOf(argument.toUpperCase());
                    newBand.setGenre(mg);
                } else {
                    setGenre(newBand, scanner, scrArg);
                }
            }
        }

        public void setX(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            XCoordinateValidator xCoordinateValidator = new XCoordinateValidator();
            if (Objects.equals(scrArg, "")) {
                System.out.print("\nВведите значение поля Coordinates.x: ");
                argument = readConsole(scanner);
                if (xCoordinateValidator.validate(argument)) {
                    coordinates.setX(parseDouble(argument));
                } else {
                    setX(newBand, scanner, scrArg);
                }
            } else {
                argument = (String) scrArg;
                if (xCoordinateValidator.validate(argument)) {
                    coordinates.setX(parseDouble(argument));
                } else {
                    setX(newBand, scanner, scrArg);
                }
            }
        }

        public void setY(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            YCoordinateValidator yCoordinateValidator = new YCoordinateValidator();
            if (Objects.equals(scrArg, "")) {
                System.out.print("\nВведите значение поля Coordinates.y: ");
                argument = readConsole(scanner);
                if (yCoordinateValidator.validate(argument)) {
                    coordinates.setY(parseFloat(argument));
                } else {
                    setY(newBand, scanner, scrArg);
                }
            } else {
                argument = (String) scrArg;
                if (yCoordinateValidator.validate(argument)) {
                    coordinates.setY(parseFloat(argument));
                } else {
                    setY(newBand, scanner, scrArg);
                }
            }
        }

        public void setStudio(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            if (Objects.equals(scrArg, "")) {
                System.out.print("\nВведите значение поля Studio.name: ");
                argument = readConsole(scanner);
                studio.setName(argument);
                newBand.setStudio(studio);
            } else {
                argument = (String) scrArg;
                studio.setName(argument);
                newBand.setStudio(studio);
            }
        }

        public void setNOP(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException, ClassNotFoundException {
            NOPValidator nopValidator = new NOPValidator();
            if (Objects.equals(scrArg, "")) {
                System.out.print("\nВведите значение поля NumberOfParticipants: ");
                argument = readConsole(scanner);
                if (nopValidator.validate(argument)) {
                    newBand.setNOP(parseLong(argument));
                } else {
                    setNOP(newBand, scanner, scrArg);
                }
            } else {
                argument = (String) scrArg;
                if (nopValidator.validate(argument)) {
                    newBand.setNOP(parseLong(argument));
                } else {
                    System.out.println("Введён неправильный тип данных.\n");
                    setNOP(newBand, scanner, scrArg);
                }
            }
        }

        public void setDate(MusicBand newBand, BufferedReader scanner, Object scrArg) throws IOException {
            newBand.setCreationDate(LocalDate.now());
        }

        public MusicBand done(MusicBand newBand) throws IOException {
            newBand.setCoordinates(coordinates);
            newBand.setStudio(studio);
            return newBand;
        }

        public String checkLine(String sc) throws IOException {
            argument = sc;
            if (argument == null) {
                System.exit(1);
            } else {
                return argument;
            }
            return argument;
        }

        public String readConsole(BufferedReader scanner) throws IOException {
            String argument = scanner.readLine();
            if(argument == null){
                System.exit(0);
            }
            return argument;
        }
}

