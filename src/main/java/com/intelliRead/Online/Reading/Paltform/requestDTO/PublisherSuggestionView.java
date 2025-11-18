package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.PublisherAction;
import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherSuggestionView {
    private int suggestionId;
    private String suggestedTitle;
    private String author;
    private String suggestionReason;
    private SuggestionStatus suggestionStatus;
    private LocalDateTime suggestionCreatedAt;

    // User who suggested
    private int suggestedByUserId;
    private String suggestedByUserName;

    // Stats
    private int totalUpvotes;
    private int totalPublisherInterests;

    // Current publisher's action
    private PublisherAction publisherAction;
    private LocalDateTime publisherActionDate;
    private String publisherNotes;

    // If book was uploaded
    private Integer uploadedBookId;
    private String uploadedBookTitle;
    private String uploadedBookStatus;
}