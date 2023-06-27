package ru.clevertec.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.clevertec.user.data.Authority;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.User;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        return UserDto.newBuilder()
                .setUsername(user.getUsername())
                .addAllAuthorities(user.getRole().getAuthorities()
                        .stream()
                        .map(a -> Authority.newBuilder()
                                .setAuthority(a.getAuthority())
                                .build())
                        .toList())
                .build();
    }

    @SneakyThrows
    public <T> T getTestData(Class<T> t, String filename) {
        URL path = TestDataUtil.class.getResource(filename);
        Objects.requireNonNull(path, "Can not find resources/" + filename);
        String json = Files.readString(Paths.get(path.toURI()));

        Message.Builder builder = null;
        try {
            // Since we are dealing with a Message type, we can call newBuilder()
            builder = (Message.Builder) t.getMethod("newBuilder").invoke(null);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                 | NoSuchMethodException | SecurityException e) {
            return MAPPER.readValue(json, t);
        }

        // The instance is placed into the builder values
        JsonFormat.parser().ignoringUnknownFields().merge(json, builder);

        // the instance will be from the build
        return (T) builder.build();
    }
}

