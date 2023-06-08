package validators;

import static java.lang.Long.parseLong;

public class NOPValidator implements Validator {
    public boolean validate(String value) {
        try {
            if (parseLong(value) <= 0) {
                System.out.println("Введите значение больше 0.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Введён неправильный тип данных. Требуется: Long.\n");
            return false;
        }
    }
}
