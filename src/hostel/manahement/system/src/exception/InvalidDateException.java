package exception;

/**
 * Thrown when check-in or check-out dates are invalid.
 * e.g. check-out is before check-in, or check-in is in the past.
 */
public class InvalidDateException extends Exception {
    public InvalidDateException(String message) {
        super(message);
    }
}
