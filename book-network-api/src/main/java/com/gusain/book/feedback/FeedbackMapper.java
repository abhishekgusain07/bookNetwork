package com.gusain.book.feedback;

import com.gusain.book.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(
                        Book.builder()
                                .id(request.bookId())
                                .archived(false) //not required, has not impact, just to satisfy lombok
                                .shareable(false)
                                .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback f, Integer userId) {
        return FeedbackResponse.builder()
                .note(f.getNote())
                .comment(f.getComment())
                .ownFeedback(Objects.equals(userId, f.getCreatedBy()))
                .build();
    }
}
