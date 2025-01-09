package com.gusain.book.book;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name="book")
public class BookController {
    private BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @RequestBody @Valid BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.saveBook(request, connectedUser));
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable("book-id") Integer bookId
    ) {
        return ResponseEntity.ok(service.findBook(bookId));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(service.findAllBooks());
    }
}
