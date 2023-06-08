package validators.coordinatesValidators;

import validators.Validator;

import static java.lang.Double.parseDouble;

public class XCoordinateValidator implements Validator {
    public boolean validate(String value) {
        try {
            parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Введён неправильный тип данных. Требуется: Double.\n");
            return false;
        }
    }
}
