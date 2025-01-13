package com.gusain.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbacRepository extends JpaRepository<Feedback, Integer> {

    @Query("""
    SELECT fb
    FROM Feedback fb
    WHERE fb.book.id = :bookId
""")
    Page<Feedback> findAllByBookId(@Param("bookId") Integer bookId, Pageable pageable);
}
