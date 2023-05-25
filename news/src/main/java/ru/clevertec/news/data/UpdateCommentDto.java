package ru.clevertec.news.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateCommentDto(

        @NotBlank
        @Pattern(regexp = "[\\w\\sа-яА-ЯёЁ,.\\-()!?:<=%>*+/]{1,255}")
        String text
) {
}
