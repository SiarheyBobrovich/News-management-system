package ru.clevertec.exception.handling.exception;

public class NewsNotFoundException extends AbstractExceptionMessageException {

    public NewsNotFoundException(Long id) {
        super(String.format("News %d not found.", id));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
