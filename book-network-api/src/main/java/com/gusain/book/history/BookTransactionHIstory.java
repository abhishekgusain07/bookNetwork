package com.gusain.book.history;

import com.gusain.book.book.Book;
import com.gusain.book.common.BaseEntity;
import com.gusain.book.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookTransactionHIstory  extends BaseEntity {

    //TODO: user relationship
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    //TODO: book relationship
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private boolean returned;
    private boolean returnApproved;
}

