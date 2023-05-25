package ru.clevertec.exception.handling.exception;

public class InvalidTokenException extends AbstractExceptionMessageException {

    public InvalidTokenException(String message) {
        super(String.format("Token: %s is not valid", message));
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
