package com.gusain.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository  extends JpaRepository<Book, Integer> {
    @Query("""
        SELECT b FROM Book b 
        WHERE b.archived = false
        AND  b.shareable = true
        AND b.owner.id != :userId
""")
    Page<Book> findAllDisplayableBook(Pageable pageable, Integer userId);
}
