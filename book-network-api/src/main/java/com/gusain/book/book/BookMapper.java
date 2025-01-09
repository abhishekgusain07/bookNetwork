package com.gusain.book.book;

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
                //TODO: implement cover for book
//                .cover()
                .archived(book.isArchived())
                .owner(book.getOwner().fullName())
                .rate(book.getRate())
                .build();
    }
}
