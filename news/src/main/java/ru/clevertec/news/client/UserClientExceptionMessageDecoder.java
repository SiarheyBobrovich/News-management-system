package ru.clevertec.news.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.clevertec.exception.handling.exception.AbstractExceptionMessageException;
import ru.clevertec.exception.handling.exception.ExceptionMessage;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClientExceptionMessageDecoder implements ErrorDecoder {

    private final ObjectMapper mapper;

    /**
     * Декодирует ответ от удаленного сервиса в исключение, если статус ответа не успешный.
     *
     * @param methodKey строка, содержащая ключ метода, который вызвал удаленный сервис
     * @param response  объект {@link Response}, содержащий ответ от удаленного сервиса
     * @return объект {@link Exception}, содержащий информацию об ошибке
     */
    @Override
    @SneakyThrows
    public Exception decode(String methodKey, Response response) {
        Response.Body body = response.body();
        Exception exception;

        if (Objects.isNull(body)) {
            exception = Optional.of(response.status())
                    .map(HttpStatus::valueOf)
                    .map(httpStatus ->
                            getException(httpStatus.value(), httpStatus.getReasonPhrase()))
                    .orElseThrow();
        } else {
            InputStream inputStream = body.asInputStream();
            ExceptionMessage exceptionMessage = mapper.readValue(inputStream, ExceptionMessage.class);
            exception = getException(exceptionMessage.status(), exceptionMessage.message());
        }
        return exception;
    }

    /**
     * Возвращает объект {@link Exception}, соответствующий заданному статусу и сообщению об ошибке.
     *
     * @param status  целое число, содержащее статус ответа от удаленного сервиса
     * @param message строка, содержащая сообщение об ошибке от удаленного сервиса
     * @return объект {@link Exception}, содержащий информацию об ошибке
     */
    private Exception getException(Integer status, String message) {
        return new AbstractExceptionMessageException(message) {
            @Override
            public int getStatusCode() {
                return status;
            }
        };
    }
}
