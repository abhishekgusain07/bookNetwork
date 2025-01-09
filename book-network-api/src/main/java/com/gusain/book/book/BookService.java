package com.gusain.book.book;

import com.gusain.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    public Integer saveBook(BookRequest request, Authentication connectedUser) {

        User user = (User)connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBook(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::fromBook)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with Id:: "+bookId));
    }

    public List<BookResponse> findAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::fromBook)
                .collect(Collectors.toList());
    }
}
