package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        users.values()
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .forEach(u -> {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                });
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User update(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getUsername() == null || newUser.getPassword() == null) {
                throw new ConditionsNotMetException("Email, имя пользователя и пароль должны быть указаны");
            }
            users.values()
                    .stream()
                    .filter(u -> u.getEmail().equals(newUser.getEmail()))
                    .forEach(u -> {
                        throw new DuplicatedDataException("Этот имейл уже используется");
                    });
            // если пользователь найден и все условия соблюдены, обновляем её содержимое
            User user = users.get(newUser.getId());
            user.setUsername(newUser.getUsername());
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());

            return user;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public User findById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь № %d не найден", id));
        } else {
            return user;
        }
    }

}