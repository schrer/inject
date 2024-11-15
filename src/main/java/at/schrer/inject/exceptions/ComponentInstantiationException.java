package at.schrer.inject.exceptions;

public class ComponentInstantiationException extends RuntimeException {
    public ComponentInstantiationException(String message) {
        super(message);
    }

    public ComponentInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentInstantiationException(Throwable cause) {
        super(cause);
    }
}
