package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    /*  @GetMapping
    public Collection<Post> findAll() {
        return postService.findAll();
    } */

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort
    ) {
        // Возвращаем посты с учетом параметров или по умолчанию
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