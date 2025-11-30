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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
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

    private final Map<Integer, Map<String, Object>> suggestionStatsCache = new ConcurrentHashMap<>();
    private static final int CACHE_MAX_SIZE = 1000;
    private static final int POPULAR_SUGGESTIONS_LIMIT = 50;

    public String saveSuggestion(SuggestionRequestDTO suggestionRequestDTO) {
        if (suggestionRequestDTO.getSuggestedTitle() == null || suggestionRequestDTO.getSuggestedTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        User user = userRepository.findById(suggestionRequestDTO.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Suggestion suggestion = SuggestionConverter.convertSuggestionRequestDtoIntoSuggestion(suggestionRequestDTO);
        suggestion.setUser(user);
        suggestionRepository.save(suggestion);
        notifyPublishersAsync(suggestion);
        return "Suggestion Saved Successfully";
    }

    public List<PublisherSuggestionView> getSuggestionsForPublishers(int publisherId) {
        List<Suggestion> suggestions = suggestionRepository.findBySuggestionStatus(SuggestionStatus.PENDING);
        if (suggestions.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> suggestionIds = suggestions.stream().map(Suggestion::getId).collect(Collectors.toList());
        Map<Integer, Integer> upvoteCounts = batchGetUpvoteCounts(suggestionIds);
        Map<Integer, Integer> interestCounts = batchGetInterestCounts(suggestionIds);
        Map<Integer, PublisherSuggestionAction> publisherActions = batchGetPublisherActions(publisherId, suggestionIds);
        List<PublisherSuggestionView> result = new ArrayList<>(suggestions.size());
        for (Suggestion suggestion : suggestions) {
            int suggestionId = suggestion.getId();
            PublisherSuggestionView view = BookSuggestionConverter.convertToPublisherView(suggestion, upvoteCounts.getOrDefault(suggestionId, 0), interestCounts.getOrDefault(suggestionId, 0), publisherActions.get(suggestionId));
            result.add(view);
        }
        return result;
    }

    private Map<Integer, Integer> batchGetUpvoteCounts(List<Integer> suggestionIds) {
        return userSuggestionVoteRepository.countUpvotesBySuggestionIds(suggestionIds).stream().collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> ((Long) obj[1]).intValue()));
    }

    private Map<Integer, Integer> batchGetInterestCounts(List<Integer> suggestionIds) {
        return publisherSuggestionActionRepository.countInterestsBySuggestionIds(suggestionIds).stream().collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> ((Long) obj[1]).intValue()));
    }

    private Map<Integer, PublisherSuggestionAction> batchGetPublisherActions(int publisherId, List<Integer> suggestionIds) {
        return publisherSuggestionActionRepository.findByPublisherIdAndSuggestionIdIn(publisherId, suggestionIds).stream().collect(Collectors.toMap(action -> action.getSuggestion().getId(), action -> action));
    }

    public String expressInterest(int publisherId, int suggestionId, String notes) {
        try {
            User publisher = userRepository.findById(publisherId).orElseThrow(() -> new UserNotFoundException("Publisher not found"));
            Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
            Optional<PublisherSuggestionAction> existingAction = publisherSuggestionActionRepository.findByPublisherIdAndSuggestionId(publisherId, suggestionId);
            PublisherSuggestionAction action;
            if (existingAction.isPresent()) {
                action = existingAction.get();
                action.setAction(PublisherAction.INTERESTED);
                action.setPublisherNotes(notes);
            } else {
                action = new PublisherSuggestionAction();
                action.setPublisher(publisher);
                action.setSuggestion(suggestion);
                action.setAction(PublisherAction.INTERESTED);
                action.setPublisherNotes(notes);
                action.setCreatedAt(LocalDateTime.now());
            }
            action.setUpdatedAt(LocalDateTime.now());
            publisherSuggestionActionRepository.save(action);
            suggestionStatsCache.remove(suggestionId);
            notifyUserAboutInterestAsync(suggestion, publisher);
            return existingAction.isPresent() ? "Interest updated successfully" : "Interest expressed successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error expressing interest: " + e.getMessage());
        }
    }

    public String uploadBookForSuggestion(int publisherId, int suggestionId, int bookId, String notes) {
        try {
            User publisher = userRepository.findById(publisherId).orElseThrow(() -> new UserNotFoundException("Publisher not found"));
            Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
            PublisherSuggestionAction action = new PublisherSuggestionAction();
            action.setPublisher(publisher);
            action.setSuggestion(suggestion);
            action.setAction(PublisherAction.UPLOADED);
            action.setPublisherNotes(notes);
            action.setUploadedBookId(bookId);
            action.setCreatedAt(LocalDateTime.now());
            publisherSuggestionActionRepository.save(action);
            suggestionStatsCache.remove(suggestionId);
            notifyAdminAboutBookUploadAsync(suggestion, publisher, bookId);
            return "Book uploaded successfully for this suggestion";
        } catch (Exception e) {
            return "Error uploading book: " + e.getMessage();
        }
    }

    public String upvoteSuggestion(int userId, int suggestionId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
            Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
            Optional<UserSuggestionVote> existingVote = userSuggestionVoteRepository.findByUserIdAndSuggestionId(userId, suggestionId);
            UserSuggestionVote vote;
            boolean wasUpvoted;
            if (existingVote.isPresent()) {
                vote = existingVote.get();
                wasUpvoted = vote.isUpvoted();
                vote.setUpvoted(!wasUpvoted);
            } else {
                vote = new UserSuggestionVote();
                vote.setUser(user);
                vote.setSuggestion(suggestion);
                vote.setUpvoted(true);
                vote.setVotedAt(LocalDateTime.now());
                wasUpvoted = false;
            }
            userSuggestionVoteRepository.save(vote);
            suggestionStatsCache.remove(suggestionId);
            return existingVote.isPresent() ? (vote.isUpvoted() ? "Suggestion upvoted" : "Suggestion unvoted") : "Suggestion upvoted successfully";
        } catch (Exception e) {
            return "Error upvoting suggestion: " + e.getMessage();
        }
    }

    public Map<String, Object> getSuggestionStats(int suggestionId) {
        Map<String, Object> cachedStats = suggestionStatsCache.get(suggestionId);
        if (cachedStats != null) {
            return new HashMap<>(cachedStats);
        }
        Optional<Object[]> statsData = suggestionRepository.findSuggestionDetailsWithStats(suggestionId);
        Map<String, Object> stats = new HashMap<>();
        if (statsData.isPresent()) {
            Object[] data = statsData.get();
            int upvoteCount = ((Long) data[3]).intValue();
            int interestCount = ((Long) data[4]).intValue();
            int uploadedCount = ((Long) data[5]).intValue();
            stats.put("upvoteCount", upvoteCount);
            stats.put("publisherInterestCount", interestCount);
            stats.put("booksUploadedCount", uploadedCount);
            stats.put("totalEngagement", upvoteCount + interestCount);
        } else {
            int upvoteCount = userSuggestionVoteRepository.countBySuggestionIdAndUpvotedTrue(suggestionId);
            int interestCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(suggestionId, PublisherAction.INTERESTED);
            int uploadedCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(suggestionId, PublisherAction.UPLOADED);
            stats.put("upvoteCount", upvoteCount);
            stats.put("publisherInterestCount", interestCount);
            stats.put("booksUploadedCount", uploadedCount);
            stats.put("totalEngagement", upvoteCount + interestCount);
        }
        if (suggestionStatsCache.size() >= CACHE_MAX_SIZE) {
            Iterator<Integer> iterator = suggestionStatsCache.keySet().iterator();
            if (iterator.hasNext()) {
                suggestionStatsCache.remove(iterator.next());
            }
        }
        suggestionStatsCache.put(suggestionId, new HashMap<>(stats));
        return stats;
    }

    public List<BookSuggestionDTO> getPopularSuggestions(int limit) {
        int actualLimit = Math.min(limit, POPULAR_SUGGESTIONS_LIMIT);
        Pageable pageable = PageRequest.of(0, actualLimit);
        Page<Object[]> popularSuggestions = suggestionRepository.findPopularSuggestionsWithStats(pageable);
        return popularSuggestions.getContent().stream().map(this::convertToBookSuggestionDTO).collect(Collectors.toList());
    }

    private BookSuggestionDTO convertToBookSuggestionDTO(Object[] data) {
        BookSuggestionDTO dto = new BookSuggestionDTO();
        dto.setId((Integer) data[0]);
        dto.setSuggestedTitle((String) data[1]);
        dto.setAuthor((String) data[2]);
        dto.setUpvoteCount(((Long) data[3]).intValue());
        dto.setPublisherInterestCount(((Long) data[4]).intValue());
        return dto;
    }

    private void notifyPublishersAsync(Suggestion suggestion) {
        new Thread(() -> {
            try {
                System.out.println("📢 Notifying publishers about new suggestion: " + suggestion.getSuggestedTitle());
            } catch (Exception e) {
                System.out.println("Failed to send notification: " + e.getMessage());
            }
        }).start();
    }

    private void notifyUserAboutInterestAsync(Suggestion suggestion, User publisher) {
        new Thread(() -> {
            try {
                System.out.println("🎯 Publisher " + publisher.getName() + " is interested in your suggestion: " + suggestion.getSuggestedTitle());
                if (emailService != null) {
                    emailService.sendPublisherInterestEmail(suggestion.getUser(), suggestion, publisher);
                }
            } catch (Exception e) {
                System.out.println("Failed to send interest notification: " + e.getMessage());
            }
        }).start();
    }

    private void notifyAdminAboutBookUploadAsync(Suggestion suggestion, User publisher, int bookId) {
        new Thread(() -> {
            try {
                System.out.println("📚 Publisher " + publisher.getName() + " uploaded book for suggestion: " + suggestion.getSuggestedTitle());
            } catch (Exception e) {
                System.out.println("Failed to send upload notification: " + e.getMessage());
            }
        }).start();
    }

    public Suggestion findSuggestionById(int id) {
        return suggestionRepository.findById(id).orElse(null);
    }

    public List<Suggestion> findAllSuggestion() {
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

    public String updateSuggestion(int id, SuggestionRequestDTO suggestionRequestDTO) {
        Suggestion suggestion = findSuggestionById(id);
        if (suggestion != null) {
            if (suggestionRequestDTO.getSuggestedTitle() == null || suggestionRequestDTO.getSuggestedTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Book title is required");
            }
            suggestion.setSuggestedTitle(suggestionRequestDTO.getSuggestedTitle());
            suggestion.setAuthor(suggestionRequestDTO.getAuthor());
            suggestion.setSuggestionStatus(suggestionRequestDTO.getSuggestionStatus());
            suggestionRepository.save(suggestion);
            suggestionStatsCache.remove(id);
            return "Suggestion Updated Successfully";
        } else {
            throw new IllegalArgumentException("Suggestion not found");
        }
    }

    public String deleteSuggestion(int id) {
        Suggestion suggestion = findSuggestionById(id);
        if (suggestion != null) {
            userSuggestionVoteRepository.deleteAllBySuggestionId(id);
            publisherSuggestionActionRepository.deleteAllBySuggestionId(id);
            suggestionRepository.deleteById(id);
            suggestionStatsCache.remove(id);
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
            suggestionStatsCache.remove(suggestionId);
            sendApprovalEmailAsync(suggestion);
            return "Suggestion approved successfully";
        } catch (Exception e) {
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
            suggestionStatsCache.remove(suggestionId);
            sendRejectionEmailAsync(suggestion);
            return "Suggestion rejected successfully";
        } catch (Exception e) {
            return "Error rejecting suggestion: " + e.getMessage();
        }
    }

    private void sendApprovalEmailAsync(Suggestion suggestion) {
        new Thread(() -> {
            try {
                emailService.sendSuggestionApprovalEmail(suggestion.getUser(), suggestion);
                System.out.println("Approval email sent to: " + suggestion.getUser().getEmail());
            } catch (Exception e) {
                System.out.println("Failed to send approval email: " + e.getMessage());
            }
        }).start();
    }

    private void sendRejectionEmailAsync(Suggestion suggestion) {
        new Thread(() -> {
            try {
                emailService.sendSuggestionRejectionEmail(suggestion.getUser(), suggestion);
                System.out.println("Rejection email sent to: " + suggestion.getUser().getEmail());
            } catch (Exception e) {
                System.out.println("Failed to send rejection email: " + e.getMessage());
            }
        }).start();
    }

    public List<Suggestion> getSuggestionsByStatus(String status) {
        try {
            SuggestionStatus suggestionStatus = SuggestionStatus.valueOf(status.toUpperCase());
            return suggestionRepository.findBySuggestionStatus(suggestionStatus);
        } catch (IllegalArgumentException e) {
            return findAllSuggestion();
        }
    }

    public List<PublisherSuggestionView> getAllSuggestionsForPublishers() {
        try {
            List<Suggestion> suggestions = suggestionRepository.findBySuggestionStatus(SuggestionStatus.APPROVED);
            if (suggestions.isEmpty()) {
                return Collections.emptyList();
            }
            List<Integer> suggestionIds = suggestions.stream().map(Suggestion::getId).collect(Collectors.toList());
            Map<Integer, Integer> upvoteCounts = batchGetUpvoteCounts(suggestionIds);
            Map<Integer, Integer> interestCounts = batchGetInterestCounts(suggestionIds);
            List<PublisherSuggestionView> result = new ArrayList<>(suggestions.size());
            for (Suggestion suggestion : suggestions) {
                int suggestionId = suggestion.getId();
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
                view.setTotalUpvotes(upvoteCounts.getOrDefault(suggestionId, 0));
                view.setTotalPublisherInterests(interestCounts.getOrDefault(suggestionId, 0));
                result.add(view);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error loading suggestions for publishers: " + e.getMessage());
        }
    }

    public List<PublisherSuggestionView> getSuggestionsForPublisher(int publisherId) {
        try {
            List<PublisherSuggestionView> allSuggestions = getAllSuggestionsForPublishers();
            if (allSuggestions.isEmpty()) {
                return Collections.emptyList();
            }
            List<Integer> suggestionIds = allSuggestions.stream().map(PublisherSuggestionView::getSuggestionId).collect(Collectors.toList());
            Map<Integer, PublisherSuggestionAction> publisherActions = batchGetPublisherActions(publisherId, suggestionIds);
            for (PublisherSuggestionView suggestion : allSuggestions) {
                PublisherSuggestionAction action = publisherActions.get(suggestion.getSuggestionId());
                if (action != null) {
                    suggestion.setPublisherAction(action.getAction());
                    suggestion.setPublisherActionDate(action.getCreatedAt());
                    suggestion.setPublisherNotes(action.getPublisherNotes());
                    suggestion.setUploadedBookId(action.getUploadedBookId());
                }
            }
            return allSuggestions;
        } catch (Exception e) {
            throw new RuntimeException("Error loading publisher suggestions: " + e.getMessage());
        }
    }

    public String markSuggestionAsUploaded(int publisherId, int suggestionId) {
        try {
            User publisher = userRepository.findById(publisherId).orElseThrow(() -> new UserNotFoundException("Publisher not found"));
            Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
            Optional<PublisherSuggestionAction> existingAction = publisherSuggestionActionRepository.findByPublisherIdAndSuggestionId(publisherId, suggestionId);
            PublisherSuggestionAction action;
            if (existingAction.isPresent()) {
                action = existingAction.get();
                action.setAction(PublisherAction.UPLOADED);
            } else {
                action = new PublisherSuggestionAction();
                action.setPublisher(publisher);
                action.setSuggestion(suggestion);
                action.setAction(PublisherAction.UPLOADED);
                action.setCreatedAt(LocalDateTime.now());
            }
            action.setUpdatedAt(LocalDateTime.now());
            publisherSuggestionActionRepository.save(action);
            suggestionStatsCache.remove(suggestionId);
            notifyAboutBookUploadAsync(suggestion, publisher);
            return "Suggestion marked as uploaded successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error marking as uploaded: " + e.getMessage());
        }
    }

    public PublisherSuggestionView getSuggestionDetails(int suggestionId) {
        try {
            Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
            int upvoteCount = userSuggestionVoteRepository.countBySuggestionIdAndUpvotedTrue(suggestionId);
            int interestCount = publisherSuggestionActionRepository.countBySuggestionIdAndAction(suggestionId, PublisherAction.INTERESTED);
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
            view.setTotalUpvotes(upvoteCount);
            view.setTotalPublisherInterests(interestCount);
            return view;
        } catch (Exception e) {
            throw new RuntimeException("Error loading suggestion details: " + e.getMessage());
        }
    }

    private void notifyAboutBookUploadAsync(Suggestion suggestion, User publisher) {
        new Thread(() -> {
            try {
                System.out.println("📚 Publisher " + publisher.getName() + " uploaded book for suggestion: " + suggestion.getSuggestedTitle());
            } catch (Exception e) {
                System.out.println("Failed to send upload notification: " + e.getMessage());
            }
        }).start();
    }
}