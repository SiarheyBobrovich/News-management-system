package ru.clevertec.user.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserDto(

        @NotNull
        @Pattern(regexp = "^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{5,39}$")
        String login,

        @NotNull
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,198}$") //Minimum eight characters, at least one letter and one number
        String password,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 2, max = 40)
        String firstName,

        @NotBlank
        @Size(min = 2, max = 50)
        String lastName
) {
}
