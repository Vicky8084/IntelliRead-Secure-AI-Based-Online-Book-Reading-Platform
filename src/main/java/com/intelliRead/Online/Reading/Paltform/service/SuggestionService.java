package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookSuggestionConverter;
import com.intelliRead.Online.Reading.Paltform.converter.SuggestionConverter;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.BookSuggestionResponse;
import com.intelliRead.Online.Reading.Paltform.requestDTO.PublisherSuggestionView;
import com.intelliRead.Online.Reading.Paltform.enums.PublisherAction;
import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.PublisherSuggestionAction;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.model.UserSuggestionVote;
import com.intelliRead.Online.Reading.Paltform.repository.PublisherSuggestionActionRepository;
import com.intelliRead.Online.Reading.Paltform.repository.SuggestionRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserSuggestionVoteRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSuggestionVoteRepository userSuggestionVoteRepository;

    @Autowired
    private PublisherSuggestionActionRepository publisherSuggestionActionRepository;

    @Autowired
    private EmailService emailService;

    // ✅ EXISTING METHODS (Updated with new functionality)
    public String saveSuggestion(SuggestionRequestDTO suggestionRequestDTO){
        if (suggestionRequestDTO.getSuggestedTitle() == null || suggestionRequestDTO.getSuggestedTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }

        Suggestion suggestion = SuggestionConverter.convertSuggestionRequestDtoIntoSuggestion(suggestionRequestDTO);
        User user = userRepository.findById(suggestionRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        suggestion.setUser(user);
        suggestionRepository.save(suggestion);

        // Notify all publishers about new suggestion
        notifyPublishersAboutNewSuggestion(suggestion);

        return "Suggestion Saved Successfully";
    }

    // ✅ NEW: Get suggestions for publishers
    public List<PublisherSuggestionView> getSuggestionsForPublishers(int publisherId) {
        List<Suggestion> suggestions = suggestionRepository.findBySuggestionStatus(SuggestionStatus.PENDING);

        return suggestions.stream()
                .map(suggestion -> {
                    int upvoteCount = userSuggestionVoteRepository.countBySuggestionIdAndUpvotedTrue(suggestion.getId());
                    int interestCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(
                            suggestion.getId(), PublisherAction.INTERESTED);

                    Optional<PublisherSuggestionAction> publisherAction =
                            publisherSuggestionActionRepository.findByPublisherIdAndSuggestionId(publisherId, suggestion.getId());

                    return BookSuggestionConverter.convertToPublisherView(
                            suggestion, upvoteCount, interestCount, publisherAction.orElse(null));
                })
                .collect(Collectors.toList());
    }

    // ✅ NEW: Publisher shows interest in suggestion
    public String expressInterest(int publisherId, int suggestionId, String notes) {
        try {
            User publisher = userRepository.findById(publisherId)
                    .orElseThrow(() -> new UserNotFoundException("Publisher not found"));

            Suggestion suggestion = suggestionRepository.findById(suggestionId)
                    .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

            // Check if publisher already expressed interest
            Optional<PublisherSuggestionAction> existingAction =
                    publisherSuggestionActionRepository.findByPublisherIdAndSuggestionId(publisherId, suggestionId);

            if (existingAction.isPresent()) {
                PublisherSuggestionAction action = existingAction.get();
                action.setAction(PublisherAction.INTERESTED);
                action.setPublisherNotes(notes);
                action.setUpdatedAt(LocalDateTime.now());
                publisherSuggestionActionRepository.save(action);
            } else {
                PublisherSuggestionAction action = new PublisherSuggestionAction();
                action.setPublisher(publisher);
                action.setSuggestion(suggestion);
                action.setAction(PublisherAction.INTERESTED);
                action.setPublisherNotes(notes);
                action.setCreatedAt(LocalDateTime.now());
                publisherSuggestionActionRepository.save(action);
            }

            // Notify the user who suggested
            notifyUserAboutPublisherInterest(suggestion, publisher);

            return "Interest expressed successfully";
        } catch (Exception e) {
            return "Error expressing interest: " + e.getMessage();
        }
    }

    // ✅ NEW: Publisher uploads book for suggestion
    public String uploadBookForSuggestion(int publisherId, int suggestionId, int bookId, String notes) {
        try {
            User publisher = userRepository.findById(publisherId)
                    .orElseThrow(() -> new UserNotFoundException("Publisher not found"));

            Suggestion suggestion = suggestionRepository.findById(suggestionId)
                    .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

            PublisherSuggestionAction action = new PublisherSuggestionAction();
            action.setPublisher(publisher);
            action.setSuggestion(suggestion);
            action.setAction(PublisherAction.UPLOADED);
            action.setPublisherNotes(notes);
            action.setUploadedBookId(bookId);
            action.setCreatedAt(LocalDateTime.now());
            publisherSuggestionActionRepository.save(action);

            // Notify admin about new book upload from suggestion
            notifyAdminAboutBookUpload(suggestion, publisher, bookId);

            return "Book uploaded successfully for this suggestion";
        } catch (Exception e) {
            return "Error uploading book: " + e.getMessage();
        }
    }

    // ✅ NEW: User upvotes suggestion
    public String upvoteSuggestion(int userId, int suggestionId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            Suggestion suggestion = suggestionRepository.findById(suggestionId)
                    .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

            Optional<UserSuggestionVote> existingVote =
                    userSuggestionVoteRepository.findByUserIdAndSuggestionId(userId, suggestionId);

            if (existingVote.isPresent()) {
                UserSuggestionVote vote = existingVote.get();
                vote.setUpvoted(!vote.isUpvoted()); // Toggle vote
                userSuggestionVoteRepository.save(vote);

                return vote.isUpvoted() ? "Suggestion upvoted" : "Suggestion unvoted";
            } else {
                UserSuggestionVote vote = new UserSuggestionVote();
                vote.setUser(user);
                vote.setSuggestion(suggestion);
                vote.setUpvoted(true);
                vote.setVotedAt(LocalDateTime.now());
                userSuggestionVoteRepository.save(vote);

                return "Suggestion upvoted successfully";
            }
        } catch (Exception e) {
            return "Error upvoting suggestion: " + e.getMessage();
        }
    }

    // ✅ NEW: Get suggestion statistics
    public Map<String, Object> getSuggestionStats(int suggestionId) {
        Map<String, Object> stats = new HashMap<>();

        int upvoteCount = userSuggestionVoteRepository.countBySuggestionIdAndUpvotedTrue(suggestionId);
        int interestCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(
                suggestionId, PublisherAction.INTERESTED);
        int uploadedCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(
                suggestionId, PublisherAction.UPLOADED);

        stats.put("upvoteCount", upvoteCount);
        stats.put("publisherInterestCount", interestCount);
        stats.put("booksUploadedCount", uploadedCount);
        stats.put("totalEngagement", upvoteCount + interestCount);

        return stats;
    }

    // ✅ NEW: Get popular suggestions
    public List<BookSuggestionDTO> getPopularSuggestions(int limit) {
        List<Suggestion> allSuggestions = suggestionRepository.findBySuggestionStatus(SuggestionStatus.PENDING);

        return allSuggestions.stream()
                .map(suggestion -> {
                    int upvoteCount = userSuggestionVoteRepository.countBySuggestionIdAndUpvotedTrue(suggestion.getId());
                    int interestCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(
                            suggestion.getId(), PublisherAction.INTERESTED);

                    return BookSuggestionConverter.convertToDTO(
                            suggestion, upvoteCount, interestCount, false, null, null);
                })
                .sorted((s1, s2) -> Integer.compare(s2.getUpvoteCount() + s2.getPublisherInterestCount(),
                        s1.getUpvoteCount() + s1.getPublisherInterestCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Notification methods
    private void notifyPublishersAboutNewSuggestion(Suggestion suggestion) {
        // Get all publishers
        List<User> publishers = userRepository.findAll().stream()
                .filter(user -> user.getRole().name().equals("PUBLISHER"))
                .collect(Collectors.toList());

        // In real implementation, send notifications/emails to all publishers
        System.out.println("📢 Notifying " + publishers.size() + " publishers about new suggestion: " + suggestion.getSuggestedTitle());
    }

    private void notifyUserAboutPublisherInterest(Suggestion suggestion, User publisher) {
        User suggestingUser = suggestion.getUser();
        System.out.println("🎯 Publisher " + publisher.getName() + " is interested in your suggestion: " + suggestion.getSuggestedTitle());

        // Send email notification to user
        try {
            emailService.sendPublisherInterestEmail(suggestingUser, suggestion, publisher);
        } catch (Exception e) {
            System.out.println("Failed to send interest email: " + e.getMessage());
        }
    }

    private void notifyAdminAboutBookUpload(Suggestion suggestion, User publisher, int bookId) {
        System.out.println("📚 Publisher " + publisher.getName() + " uploaded book for suggestion: " + suggestion.getSuggestedTitle());
        // Admin ko notify karein for approval
    }

    // Keep all existing methods from your original SuggestionService
    public Suggestion findSuggestionById(int id){
        Optional<Suggestion> suggestionOptional = suggestionRepository.findById(id);
        return suggestionOptional.orElse(null);
    }

    public List<Suggestion> findAllSuggestion(){
        return suggestionRepository.findAll();
    }

    public List<Suggestion> findPendingSuggestions() {
        return suggestionRepository.findBySuggestionStatus(SuggestionStatus.PENDING);
    }

    public List<Suggestion> findApprovedSuggestions() {
        return suggestionRepository.findBySuggestionStatus(SuggestionStatus.APPROVED);
    }

    public List<Suggestion> findRejectedSuggestions() {
        return suggestionRepository.findBySuggestionStatus(SuggestionStatus.REJECTED);
    }

    public String updateSuggestion(int id, SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion = findSuggestionById(id);
        if (suggestion != null) {
            if (suggestionRequestDTO.getSuggestedTitle() == null || suggestionRequestDTO.getSuggestedTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Book title is required");
            }

            suggestion.setSuggestedTitle(suggestionRequestDTO.getSuggestedTitle());
            suggestion.setAuthor(suggestionRequestDTO.getAuthor());
            suggestion.setSuggestionStatus(suggestionRequestDTO.getSuggestionStatus());
            suggestionRepository.save(suggestion);
            return "Suggestion Updated Successfully";
        } else {
            throw new IllegalArgumentException("Suggestion not found");
        }
    }

    public String deleteSuggestion(int id){
        Suggestion suggestion = findSuggestionById(id);
        if (suggestion != null) {
            // Delete related votes and actions first
            userSuggestionVoteRepository.deleteBySuggestionId(id);
            publisherSuggestionActionRepository.deleteBySuggestionId(id);

            suggestionRepository.deleteById(id);
            return "Suggestion deleted Successfully";
        } else {
            throw new IllegalArgumentException("Suggestion not found");
        }
    }

    public List<Suggestion> findSuggestionsByUserId(int userId) {
        return suggestionRepository.findByUserId(userId);
    }

    public String approveSuggestion(int suggestionId, String adminNotes) {
        try {
            Suggestion suggestion = findSuggestionById(suggestionId);
            if (suggestion == null) {
                return "Suggestion not found";
            }

            suggestion.setSuggestionStatus(SuggestionStatus.APPROVED);
            suggestion.setAdminNotes(adminNotes != null ? adminNotes : "No notes provided");
            suggestionRepository.save(suggestion);

            try {
                emailService.sendSuggestionApprovalEmail(suggestion.getUser(), suggestion);
                System.out.println("Approval email sent to: " + suggestion.getUser().getEmail());
            } catch (Exception e) {
                System.out.println("Failed to send approval email: " + e.getMessage());
            }

            return "Suggestion approved successfully";
        } catch (Exception e) {
            System.out.println("Error approving suggestion: " + e.getMessage());
            return "Error approving suggestion: " + e.getMessage();
        }
    }

    public String rejectSuggestion(int suggestionId, String adminNotes) {
        try {
            Suggestion suggestion = findSuggestionById(suggestionId);
            if (suggestion == null) {
                return "Suggestion not found";
            }

            suggestion.setSuggestionStatus(SuggestionStatus.REJECTED);
            suggestion.setAdminNotes(adminNotes != null ? adminNotes : "No reason provided");
            suggestionRepository.save(suggestion);

            try {
                emailService.sendSuggestionRejectionEmail(suggestion.getUser(), suggestion);
                System.out.println("Rejection email sent to: " + suggestion.getUser().getEmail());
            } catch (Exception e) {
                System.out.println("Failed to send rejection email: " + e.getMessage());
            }

            return "Suggestion rejected successfully";
        } catch (Exception e) {
            System.out.println("Error rejecting suggestion: " + e.getMessage());
            return "Error rejecting suggestion: " + e.getMessage();
        }
    }

    public List<Suggestion> getSuggestionsByStatus(String status) {
        try {
            SuggestionStatus suggestionStatus = SuggestionStatus.valueOf(status.toUpperCase());
            return suggestionRepository.findBySuggestionStatus(suggestionStatus);
        } catch (IllegalArgumentException e) {
            return findAllSuggestion();
        }
    }
}