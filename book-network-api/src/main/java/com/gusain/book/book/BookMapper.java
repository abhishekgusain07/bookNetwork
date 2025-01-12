package com.gusain.book.book;

import com.gusain.book.file.FileUtils;
import com.gusain.book.history.BookTransactionHIstory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .isbn(request.isbn())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse fromBook(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .synopsis(book.getSynopsis())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .archived(book.isArchived())
                .owner(book.getOwner().fullName())
                .rate(book.getRate())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHIstory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returnApproved(history.isReturnApproved())
                .returned(history.isReturned())
                .build();
    }
}
