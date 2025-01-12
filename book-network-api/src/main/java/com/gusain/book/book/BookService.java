package com.gusain.book.book;

import com.gusain.book.common.PageResponse;
import com.gusain.book.exception.OperationNotPermittedException;
import com.gusain.book.history.BookTransactionHIstory;
import com.gusain.book.history.BookTransactionHistoryRepository;
import com.gusain.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionRepo;

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

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBook(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::fromBook)
                .toList();
        return new PageResponse<BookResponse>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::fromBook)
                .toList();
        return new PageResponse(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHIstory> allBorrowedBooks =  bookTransactionRepo.findAllBorrowedBooks(pageable,user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                        .toList();
        return new PageResponse(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHIstory> allBorrowedBooks =  bookTransactionRepo.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateBookShareableStatus(Integer bookId, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with Id:: "+bookId));
        if(!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannot update book shareable status");
        }
        boolean currentStatus = book.isShareable();
        book.setShareable(!currentStatus);
        return bookRepository.save(book).getId();
    }

    public Integer updateBookArchiveStatus(Integer bookId, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with Id:: "+bookId));
        if(!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannot update book shareable status");
        }
        boolean currentStatus = book.isArchived();
        book.setArchived(!currentStatus);
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Authentication connectedUser, Integer bookId) {
        User user = ((User)connectedUser.getPrincipal());
        Book book = bookRepository.findById(bookId).orElse(null);
        if(Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("Cannot borrow your own book, make it archive instead");
        }
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be borrowed since it is archived or not shareable");
        }

        final boolean isAlreadyBorrowedByUser = bookTransactionRepo.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("The Requested Book is already borrowed");
        }

        final boolean isAlreadyBorrowedByOtherUser = bookTransactionRepo.isAlreadyBorrowed(bookId);
        if(isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The Requested Book is already borrowed");
        }

        BookTransactionHIstory bookTransactionHIstory = BookTransactionHIstory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionRepo.save(bookTransactionHIstory).getId();
    }

    public Integer returnBook(Authentication connectedUser, Integer bookId) {
        User user = ((User)connectedUser.getPrincipal());
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with Id:: "+bookId));

        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be returned");
        }

        if(!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        final boolean isAlreadyBorrowedByOtherUser = bookTransactionRepo.isAlreadyBorrowed(bookId);
        if(isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The request book is borrowed by some other person");
        }

        //check if this book is currently borrowed
        final boolean isAlreadyBorrowedByUser = bookTransactionRepo.isAlreadyBorrowedByUser(bookId, user.getId());
        if(!isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("The Requested Book is not borrowed by you");
        }

        BookTransactionHIstory history = bookTransactionRepo.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("you did not borrow this book"));
        history.setReturned(true);
        return bookTransactionRepo.save(history).getId();
    }

    public Integer approveReturn(Integer bookId, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with Id:: "+bookId));

        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be returned");
        }

        //check if book belongs to the user
        if(!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("Cannot approve return of someone else's book");
        }

        //not archived and shareable
        BookTransactionHIstory history = bookTransactionRepo.findReturnedBookWithOwnerIdAndBookId(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("The Book is not returned yet, so you cannot approve the return ."));

        history.setReturnApproved(true);

        return bookTransactionRepo.save(history).getId();
    }
}
