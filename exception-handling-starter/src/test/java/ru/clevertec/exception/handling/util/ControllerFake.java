package ru.clevertec.exception.handling.util;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;

@RestController
@RequestMapping("/fake")
public class ControllerFake {

    @GetMapping("/comments/{id}")
    ResponseEntity<DtoFake> throwCommentException(@PathVariable Long id) {
        throw new CommentNotFoundException(id);
    }

    @GetMapping("/news/{id}")
    ResponseEntity<DtoFake> throwNewsException(@PathVariable Long id) {
        throw new NewsNotFoundException(id);
    }

    @PostMapping("/valid")
    ResponseEntity<Void> throwNewsException(@Valid @RequestBody DtoFake dto) {
        return ResponseEntity.ok().build();
    }
}
