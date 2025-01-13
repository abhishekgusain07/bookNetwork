package com.gusain.book.feedback;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse{
    private Double note;
    private String comment;
    private boolean ownFeedback;
}
