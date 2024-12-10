package at.schrer.inject.exceptions.internal;

public class ConstructionInvocationException extends Exception {
    public ConstructionInvocationException(String message) {
        super(message);
    }

    public ConstructionInvocationException(Throwable e) {
        super(e);
    }
}
