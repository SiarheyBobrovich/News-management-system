package ru.clevertec.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.exception.handling.exception.InvalidLoginException;
import ru.clevertec.exception.handling.exception.UserNotFoundException;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.mapper.UserMapper;
import ru.clevertec.user.repository.UserRepository;
import ru.clevertec.user.service.TokenService;
import ru.clevertec.user.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    /**
     * Метод для получения токена авторизации на основе логина и пароля пользователя.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return токен авторизации
     * @throws InvalidLoginException Когда пользователь не найден или пороль не соответствует
     */
    @Override
    public String getAuthorizationToken(String login,
                                        String password) throws InvalidLoginException {
        return userRepository.findByLogin(login)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(tokenService::generateToken)
                .orElseThrow(InvalidLoginException::new);
    }

    /**
     * Находит пользователя по имени пользователя. Поиск чувствителен к регистру.
     *
     * @param login имя пользователя, идентифицирующее пользователя, чьи данные требуются.
     * @return полностью заполненная запись пользователя
     * @throws UserNotFoundException — если пользователя не удалось найти
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UserNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }

    /**
     * Метод для получения {@link UserDto} объекта пользователя на основе объекта {@link UserDetails}.
     *
     * @param userDetails объект {@link UserDetails}
     * @return {@link UserDto} объект пользователя
     */
    @Override
    public UserDto getUserInfo(UserDetails userDetails) {
        return userMapper.toUserDto(userDetails);
    }


    /**
     * Метод для создания {@link ru.clevertec.user.entity.User} объекта на основе объекта {@link CreateUserDto}.
     *
     * @param dto объект {@link CreateUserDto}
     * @return объект пользователя {@link ResponseUserDto}
     * @throws InvalidLoginException когда логин уже существует
     */
    @Override
    @Transactional
    public ResponseUserDto create(CreateUserDto dto) throws InvalidLoginException {
        try {
            return Optional.of(dto)
                    .map(userMapper::toUser)
                    .map(u -> {
                        u.setPassword(passwordEncoder.encode(dto.password()));
                        return u;
                    })
                    .map(userRepository::save)
                    .map(userMapper::toUserDto)
                    .orElseThrow();
        } catch (DataAccessException e) {
            throw new InvalidLoginException("Не верный логин, измените и попробуйте ещё раз");
        }
    }
}
