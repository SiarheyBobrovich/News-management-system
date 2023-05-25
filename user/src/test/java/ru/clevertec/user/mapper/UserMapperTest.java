package ru.clevertec.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.User;
import ru.clevertec.user.utils.TestDataUtil;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void toUserDto() {
        User user = TestDataUtil.getUser();
        UserDto expectedDto = TestDataUtil.userDto();

        UserDto actualDto = userMapper.toUserDto((UserDetails) user);

        assertThat(actualDto).isEqualTo(expectedDto);
    }
}