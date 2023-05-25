package ru.clevertec.user.repository;

import org.springframework.data.repository.CrudRepository;
import ru.clevertec.user.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
