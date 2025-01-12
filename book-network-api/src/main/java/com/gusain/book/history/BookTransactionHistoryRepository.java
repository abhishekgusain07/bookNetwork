package com.gusain.book.history;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

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


    @Query("""
    select history
    from BookTransactionHIstory history
    WHERE history.book.id = :bookId
    AND history.user.id = :userId
    AND history.returned = false
    AND history.returnApproved = false
""")
    Optional<BookTransactionHIstory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

    @Query("""
    SELECT history
    from BookTransactionHIstory history
    where history.book.id = :bookId
    and history.book.owner.id = :ownerId
    and history.returned = true
    and history.returnApproved = false
""")
    Optional<BookTransactionHIstory> findReturnedBookWithOwnerIdAndBookId(@Param("bookId") Integer bookId, @Param("ownerId") Integer ownerId);
}
