package com.gusain.book.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository  extends JpaRepository<Book, Integer> {
}
