package at.schrer.inject;

public class ContextException extends Exception {
    public ContextException(String message) {
        super(message);
    }

    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
