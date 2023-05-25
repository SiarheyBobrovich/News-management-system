package ru.clevertec.news.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateCommentDto(

        @NotBlank
        @Pattern(regexp = "[\\w\\sа-яА-ЯёЁ,.\\-()!?:=%<>*+/]{1,255}")
        String text,

        @NotBlank
        @Pattern(regexp = "[\\w\\sа-яА-ЯёЁ]{1,40}")
        String username,

        @NotNull
        Long newsId
) {
}
