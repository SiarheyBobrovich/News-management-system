package ru.clevertec.exception.handling.exception;

public class InvalidLoginException extends AbstractExceptionMessageException {

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException() {
        super("Не верный логин или пороль");
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
