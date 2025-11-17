package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private int id;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
    private String userName;
    private String userEmail;
    private int bookId;
    private String bookTitle;
}