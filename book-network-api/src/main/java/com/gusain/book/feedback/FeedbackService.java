package com.gusain.book.feedback;

import com.gusain.book.book.Book;
import com.gusain.book.book.BookRepository;
import com.gusain.book.common.PageResponse;
import com.gusain.book.exception.OperationNotPermittedException;
import com.gusain.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbacRepository feedbacRepository;
    private final FeedbackMapper feedbackMapper;
    private final BookRepository bookRepository;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Cannot give feedback to archived or not shareable book");
        }

        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("Cannot give feedback to your own book");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbacRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> getFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbacRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
