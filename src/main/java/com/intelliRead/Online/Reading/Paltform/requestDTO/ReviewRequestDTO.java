package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {

    private int rating;
    private String reviewText;
    private int userId;
    private int bookId;
}
