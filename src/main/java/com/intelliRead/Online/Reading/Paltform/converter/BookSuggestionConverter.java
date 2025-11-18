package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO;
import com.intelliRead.Online.Reading.Paltform.requestDTO.PublisherSuggestionView;
import com.intelliRead.Online.Reading.Paltform.enums.PublisherAction;
import com.intelliRead.Online.Reading.Paltform.model.PublisherSuggestionAction;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.model.UserSuggestionVote;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookSuggestionConverter {

    public static BookSuggestionDTO convertToDTO(Suggestion suggestion,
                                                 int upvoteCount,
                                                 int publisherInterestCount,
                                                 boolean userHasUpvoted,
                                                 PublisherAction publisherAction,
                                                 LocalDateTime publisherActionDate) {
        BookSuggestionDTO dto = new BookSuggestionDTO();
        dto.setId(suggestion.getId());
        dto.setSuggestedTitle(suggestion.getSuggestedTitle());
        dto.setAuthor(suggestion.getAuthor());
        dto.setSuggestionReason(suggestion.getSuggestionReason());
        dto.setSuggestionStatus(suggestion.getSuggestionStatus());
        dto.setCreatedAt(suggestion.getCreatedAt());
        dto.setUpdatedAt(suggestion.getUpdatedAt());
        dto.setAdminNotes(suggestion.getAdminNotes());

        if (suggestion.getUser() != null) {
            dto.setUserId(suggestion.getUser().getId());
            dto.setUserName(suggestion.getUser().getName());
            dto.setUserEmail(suggestion.getUser().getEmail());
        }

        dto.setUpvoteCount(upvoteCount);
        dto.setPublisherInterestCount(publisherInterestCount);
        dto.setUserHasUpvoted(userHasUpvoted);
        dto.setPublisherActionStatus(publisherAction != null ? publisherAction.name() : null);
        dto.setPublisherActionDate(publisherActionDate);

        return dto;
    }

    public static PublisherSuggestionView convertToPublisherView(Suggestion suggestion,
                                                                 int totalUpvotes,
                                                                 int totalPublisherInterests,
                                                                 PublisherSuggestionAction publisherAction) {
        PublisherSuggestionView view = new PublisherSuggestionView();
        view.setSuggestionId(suggestion.getId());
        view.setSuggestedTitle(suggestion.getSuggestedTitle());
        view.setAuthor(suggestion.getAuthor());
        view.setSuggestionReason(suggestion.getSuggestionReason());
        view.setSuggestionStatus(suggestion.getSuggestionStatus());
        view.setSuggestionCreatedAt(suggestion.getCreatedAt());

        if (suggestion.getUser() != null) {
            view.setSuggestedByUserId(suggestion.getUser().getId());
            view.setSuggestedByUserName(suggestion.getUser().getName());
        }

        view.setTotalUpvotes(totalUpvotes);
        view.setTotalPublisherInterests(totalPublisherInterests);

        if (publisherAction != null) {
            view.setPublisherAction(publisherAction.getAction());
            view.setPublisherActionDate(publisherAction.getCreatedAt());
            view.setPublisherNotes(publisherAction.getPublisherNotes());
            view.setUploadedBookId(publisherAction.getUploadedBookId());
        }

        return view;
    }

    public static List<BookSuggestionDTO> convertToDTOList(List<Suggestion> suggestions,
                                                           Function<Suggestion, Integer> upvoteCountProvider,
                                                           Function<Suggestion, Integer> interestCountProvider,
                                                           Function<Suggestion, Boolean> userVoteProvider,
                                                           Function<Suggestion, PublisherAction> actionProvider,
                                                           Function<Suggestion, LocalDateTime> actionDateProvider) {
        return suggestions.stream()
                .map(suggestion -> convertToDTO(
                        suggestion,
                        upvoteCountProvider.apply(suggestion),
                        interestCountProvider.apply(suggestion),
                        userVoteProvider.apply(suggestion),
                        actionProvider.apply(suggestion),
                        actionDateProvider.apply(suggestion)
                ))
                .collect(Collectors.toList());
    }
}