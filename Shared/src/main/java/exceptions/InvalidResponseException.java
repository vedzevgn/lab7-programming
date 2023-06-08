package exceptions;

public class InvalidResponseException extends Exception {

    public InvalidResponseException() {
        this("Некорректный ответ (Response).");
    }

    public InvalidResponseException(String message) {
        super(message);
    }

}
