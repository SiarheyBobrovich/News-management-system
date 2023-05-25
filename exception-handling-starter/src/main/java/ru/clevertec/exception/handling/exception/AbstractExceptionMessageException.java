package ru.clevertec.exception.handling.exception;

public abstract class AbstractExceptionMessageException extends RuntimeException {

    public AbstractExceptionMessageException(String message) {
        super(message);
    }

    public abstract int getStatusCode();

    public ExceptionMessage getExceptionMessage() {
        return new ExceptionMessage(getStatusCode(), getMessage());
    }
}
