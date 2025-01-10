package com.gusain.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHIstory, Integer> {
    @Query("""
        select history 
        from BookTransactionHIstory history
        WHERE history.user.id = :userId
    """)
    Page<BookTransactionHIstory> findAllBorrowedBooks(Pageable pageable,@Param("userId") Integer userId);

    @Query("""
    SELECT history
    FROM BookTransactionHIstory history
    WHERE history.book.owner.id = :userId
    AND history.returned = true
""")
    Page<BookTransactionHIstory> findAllReturnedBooks(Pageable pageable,@Param("userId") Integer userId);

    @Query("""
    SELECT
    (COUNT(*)>0) AS isBorrowed
    FROM BookTransactionHIstory history
    WHERE history.user.id = :userId
    AND history.book.id = :bookId
    AND history.returnApproved = false
""")
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId, @Param("userId") Integer userId);


    @Query("""
    SELECT 
    (COUNT(*) > 0) AS isBorrowed
    FROM BookTransactionHIstory history
    WHERE history.book.id = :bookId
    AND history.returnApproved = false
""")
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);
}
