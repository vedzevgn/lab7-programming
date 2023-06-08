package validators;

import java.util.Objects;

public class NameValidator implements Validator {
    /**
     * Checks if value is not null and not blank.
     *
     * @param value name to validate
     * @return true/false - matches the restrictions
     */

    public boolean validate(String value){
        if (Objects.equals(value, "") || value == null) {
            System.out.println("Введите не пустую строку.\n");
            return false;
        } else {
            return true;
        }
    }

}
