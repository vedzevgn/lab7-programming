package validators.coordinatesValidators;

import validators.Validator;

public class YCoordinateValidator implements Validator {
    public boolean validate(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Введён неправильный тип данных. Требуется: Float.\n");
            return false;
        }
    }
}
