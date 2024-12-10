package at.schrer.inject.exceptions;

import at.schrer.inject.exceptions.internal.ComponentInstantiationException;

public class ContextException extends RuntimeException {
    public ContextException(String message) {
        super(message);
    }

    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        String suffixMessage = "";
        if (getCause() instanceof ComponentInstantiationException cie
                && cie.getBeanDescriptor()!=null) {
            suffixMessage = ": Error creating instance of bean " + cie.getBeanDescriptor();
        }
        return super.getMessage() + suffixMessage;
    }
}
