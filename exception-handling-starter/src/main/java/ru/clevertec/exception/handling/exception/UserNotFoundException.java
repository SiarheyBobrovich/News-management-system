package ru.clevertec.exception.handling.exception;

public class UserNotFoundException extends AbstractExceptionMessageException {

    public UserNotFoundException(String username) {
        super(String.format("%s not found.", username));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
