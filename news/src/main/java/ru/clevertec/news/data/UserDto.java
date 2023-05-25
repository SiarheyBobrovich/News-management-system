package ru.clevertec.news.data;

import java.util.List;

public record UserDto(String username, List<AuthorityDto> authorities) {

}
