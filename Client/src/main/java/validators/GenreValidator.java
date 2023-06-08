package validators;

import parameters.MusicGenre;

public class GenreValidator implements Validator {

    public boolean validate(String value) {
        for (MusicGenre mg : MusicGenre.values()) {
            if (mg.name().equals(value)) {
                return true;
            }
        }
        System.out.println("Введено неправильное значение.\n");
        return false;
    }
}
