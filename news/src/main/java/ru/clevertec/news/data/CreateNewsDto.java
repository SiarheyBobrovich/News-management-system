package ru.clevertec.news.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateNewsDto(

        @NotBlank
        @Pattern(regexp = "[\\w\\sа-яА-ЯёЁ,.\\-()!?:<>*+/]{5,255}")
        String title,

        @NotBlank
        @Pattern(regexp = "[\\w\\sа-яА-ЯёЁ,.\\-()!?:<>*+/]{10,}")
        String text
) {
}
