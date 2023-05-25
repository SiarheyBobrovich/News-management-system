package ru.clevertec.exception.handling.util;

import jakarta.validation.constraints.Positive;

public record DtoFake(@Positive int ids) {
}
