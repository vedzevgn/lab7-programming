package exceptions;

/**
 * Thrown to indicate that invalid arguments are given.
 */

public class InvalidArgsException extends Exception{
    public InvalidArgsException(String message) {
        super(message);
    }
}
