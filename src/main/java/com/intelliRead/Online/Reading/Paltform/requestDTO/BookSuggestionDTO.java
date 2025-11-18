package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSuggestionDTO {
    private int id;
    private String suggestedTitle;
    private String author;
    private String suggestionReason;
    private SuggestionStatus suggestionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminNotes;

    // User info
    private int userId;
    private String userName;
    private String userEmail;

    // Stats
    private int upvoteCount;
    private int publisherInterestCount;
    private boolean userHasUpvoted;

    // Publisher actions
    private String publisherActionStatus;
    private LocalDateTime publisherActionDate;
}