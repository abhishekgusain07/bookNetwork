package com.gusain.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse{
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String owner;
    private String synopsis;
    private byte[] cover;
    private double rate;
    private boolean archived;
    private boolean shareable;
}
