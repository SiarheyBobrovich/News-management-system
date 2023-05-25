package ru.clevertec.exception.handling.exception;

public class CommentNotFoundException extends AbstractExceptionMessageException {

    public CommentNotFoundException(Long id) {
        super(String.format("Comment %d not found.", id));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
