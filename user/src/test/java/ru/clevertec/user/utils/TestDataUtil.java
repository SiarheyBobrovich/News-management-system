package ru.clevertec.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.User;

import java.io.InputStream;
import java.util.Objects;

@UtilityClass
public class TestDataUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String CREATE_USER_DTO_01 = "/data/create_user_dto_01.json";
    public static final String CREATE_USER_DTO_02 = "/data/create_user_dto_02.json";
    public static final String RESPONSE_USER_DTO_01 = "/data/response_user_dto_01.json";
    public static final String RESPONSE_USER_DTO_02 = "/data/response_user_dto_02.json";
    public static final String USER_01 = "/data/user_01.json";
    public static final String USER_01_BEFORE_SAVE = "/data/user_01_before_save.json";

    public User getUser() {
        return getTestData(User.class, USER_01);
    }

    public UserDto userDto() {
        User user = getUser();
        return new UserDto(getUser().getUsername(), user.getRole().getAuthorities());
    }

    @SneakyThrows
    public <T> T getTestData(Class<T> t, String filename) {
        InputStream resourceAsStream = TestDataUtil.class.getResourceAsStream(filename);
        Objects.requireNonNull(resourceAsStream, "Can not find resources/" + filename);
        return MAPPER.readValue(resourceAsStream, t);
    }
}
