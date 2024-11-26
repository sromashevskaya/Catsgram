package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll() {
        return posts.values();
    }

    public List<Post> findAll(Integer from, Integer size, String sort) {
        return posts.values().stream()
                // Сортируем посты по дате создания
                .sorted((p0, p1) -> {
                    int comp = p0.getPostDate().compareTo(p1.getPostDate()); // Прямой порядок сортировки
                    if ("desc".equals(sort)) {
                        comp = -comp; // Обратный порядок сортировки
                    }
                    return comp;
                })
                // Пропускаем первые `from` элементов
                .skip(from)
                // Ограничиваем результат `size` элементами
                .limit(size)
                // Собираем результат в список
                .collect(Collectors.toList());
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post findById(long id) {
        Post post = posts.get(id);
        if (post == null) {
            throw new NotFoundException(String.format("Пост № %d не найден", id));
        } else {
            return post;
        }
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}