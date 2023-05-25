package ru.clevertec.news.data;

import jakarta.validation.constraints.Size;

public record Filter(@Size(min = 1) String part) {
}
