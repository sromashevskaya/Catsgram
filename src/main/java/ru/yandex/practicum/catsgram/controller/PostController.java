package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort
    ) {
        // Проверка параметра sort
        if (!"asc".equalsIgnoreCase(sort) && !"desc".equalsIgnoreCase(sort)) {
            throw new ParameterNotValidException("sort", "Некорректное значение сортировки. Допустимы только 'asc' или 'desc'.");
        }

        // Проверка параметра size
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля.");
        }

        // Проверка параметра from
        if (from < 0) {
            throw new ParameterNotValidException("from", "Некорректное значение начального индекса. Индекс не может быть меньше нуля.");
        }

        // Возвращаем посты с учетом параметров
        return postService.findAll(from, size, sort);
    }

    @ResponseStatus
    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Post findById(@PathVariable("postId") long id) {
        return postService.findById(id);
    }
}