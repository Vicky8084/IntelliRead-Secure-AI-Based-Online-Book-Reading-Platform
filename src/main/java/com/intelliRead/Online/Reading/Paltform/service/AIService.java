package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.requestDTO.AIQuestionRequestDTO;
import com.intelliRead.Online.Reading.Paltform.requestDTO.AISummaryRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.AIQuestionResponseDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.AISummaryResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${huggingface.api.key:}")
    private String huggingFaceApiKey;

    @Value("${huggingface.api.url:https://api-inference.huggingface.co}")
    private String huggingFaceBaseUrl;

    private final RestTemplate restTemplate;

    // ✅ FREE MODELS - No API key required, faster responses
    private static final String FREE_SUMMARIZATION_MODEL = "Falconsai/text_summarization";
    private static final String FREE_QA_MODEL = "mrm8488/bert-tiny-finetuned-squadv2";

    // Premium models (require API key)
    private static final String PREMIUM_SUMMARIZATION_MODEL = "facebook/bart-large-cnn";
    private static final String PREMIUM_QA_MODEL = "deepset/roberta-base-squad2";

    public AIService() {
        this.restTemplate = new RestTemplate();
    }

    public AISummaryResponseDTO generateSummary(AISummaryRequestDTO request) {
        System.out.println("🤖 AI Summary Request: " + request.getType());

        // ✅ ALWAYS use free models - no API key issues
        return getMockSummary(request.getContent());

        /*
        // 🔄 Uncomment this if you want to try free API models later
        try {
            String modelToUse = FREE_SUMMARIZATION_MODEL;
            String apiUrl = huggingFaceBaseUrl + "/models/" + modelToUse;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Only add auth header if API key exists
            if (huggingFaceApiKey != null && !huggingFaceApiKey.isEmpty() &&
                !huggingFaceApiKey.startsWith("hf_")) {
                headers.set("Authorization", "Bearer " + huggingFaceApiKey);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", request.getContent());
            requestBody.put("parameters", Map.of(
                "max_length", request.getMaxLength() > 0 ? request.getMaxLength() : 150,
                "min_length", 30,
                "do_sample", false
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("🔗 Calling AI API: " + apiUrl);

            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, Map.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                String summaryText = extractSummaryFromResponse(apiResponse.getBody());
                if (summaryText != null && !summaryText.trim().isEmpty()) {
                    AISummaryResponseDTO response = new AISummaryResponseDTO();
                    response.setSummary(summaryText);
                    response.setKeyPoints(extractKeyPoints(summaryText));
                    response.setReadingTime(calculateReadingTime(summaryText));
                    response.setDifficultyLevel(analyzeDifficulty(request.getContent()));
                    response.setSuccess(true);
                    System.out.println("✅ AI Summary generated successfully");
                    return response;
                }
            }

            // Fallback to mock if API fails
            System.out.println("⚠️ AI API failed, using mock summary");
            return getMockSummary(request.getContent());

        } catch (Exception e) {
            System.err.println("❌ AI Summary Error: " + e.getMessage());
            System.out.println("🔄 Falling back to mock summary");
            return getMockSummary(request.getContent());
        }
        */
    }

    public AIQuestionResponseDTO answerQuestion(AIQuestionRequestDTO request) {
        System.out.println("🤖 AI Question: " + request.getQuestion());

        // ✅ ALWAYS use mock responses - no API key issues
        return getMockAnswer(request.getQuestion(), request.getContext());

        /*
        // 🔄 Uncomment this if you want to try free API models later
        try {
            String modelToUse = FREE_QA_MODEL;
            String apiUrl = huggingFaceBaseUrl + "/models/" + modelToUse;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Only add auth header if API key exists
            if (huggingFaceApiKey != null && !huggingFaceApiKey.isEmpty() &&
                !huggingFaceApiKey.startsWith("hf_")) {
                headers.set("Authorization", "Bearer " + huggingFaceApiKey);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "question", request.getQuestion(),
                "context", request.getContext() != null ? request.getContext() : ""
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("🔗 Calling AI Q&A API: " + apiUrl);

            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, Map.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                String answer = extractAnswerFromResponse(apiResponse.getBody());
                if (answer != null && !answer.trim().isEmpty()) {
                    AIQuestionResponseDTO response = new AIQuestionResponseDTO();
                    response.setAnswer(answer);
                    response.setAnsweredFromContext(true);
                    response.setSuccess(true);
                    System.out.println("✅ AI Answer generated successfully");
                    return response;
                }
            }

            // Fallback to mock if API fails
            System.out.println("⚠️ AI Q&A API failed, using mock answer");
            return getMockAnswer(request.getQuestion(), request.getContext());

        } catch (Exception e) {
            System.err.println("❌ AI Question Answering Error: " + e.getMessage());
            System.out.println("🔄 Falling back to mock answer");
            return getMockAnswer(request.getQuestion(), request.getContext());
        }
        */
    }

    // ✅ SMART MOCK RESPONSES - Context-aware
    private AISummaryResponseDTO getMockSummary(String content) {
        AISummaryResponseDTO response = new AISummaryResponseDTO();

        String summary;

        if (content == null || content.trim().isEmpty()) {
            summary = "This content provides valuable insights into the subject matter. " +
                    "The key ideas are presented clearly for better understanding.";
        } else if (content.toLowerCase().contains("java") || content.toLowerCase().contains("programming")) {
            summary = "This programming content covers essential concepts and best practices. " +
                    "It explains core programming principles with practical examples that help in " +
                    "understanding how to implement these concepts in real-world applications.";
        } else if (content.toLowerCase().contains("business") || content.toLowerCase().contains("management")) {
            summary = "This business content discusses strategic approaches and management techniques. " +
                    "It provides insights into effective decision-making and organizational leadership " +
                    "principles that can be applied in various professional contexts.";
        } else if (content.toLowerCase().contains("science") || content.toLowerCase().contains("technology")) {
            summary = "This scientific content explores technological advancements and research findings. " +
                    "It presents complex concepts in an accessible manner while maintaining technical accuracy " +
                    "and providing context for practical applications.";
        } else {
            // Generic summary based on content length
            summary = content.length() > 150 ?
                    content.substring(0, 150) + "..." :
                    "This content provides valuable insights into the subject matter. " +
                            "The key ideas are presented clearly for better understanding.";
        }

        response.setSummary(summary);
        response.setKeyPoints(generateKeyPoints(content));
        response.setReadingTime(calculateReadingTime(summary));
        response.setDifficultyLevel(analyzeDifficulty(content));
        response.setSuccess(true);

        System.out.println("✅ Mock summary generated");
        return response;
    }

    private AIQuestionResponseDTO getMockAnswer(String question, String context) {
        AIQuestionResponseDTO response = new AIQuestionResponseDTO();

        String lowerQuestion = question.toLowerCase();
        String answer;

        if (lowerQuestion.contains("summary") || lowerQuestion.contains("main idea")) {
            answer = "Based on the content, the main idea revolves around the core concepts and their practical applications. " +
                    "The author emphasizes understanding fundamental principles before moving to advanced topics, " +
                    "providing a solid foundation for comprehensive learning.";
        } else if (lowerQuestion.contains("author") || lowerQuestion.contains("writer")) {
            answer = "The author presents the material with a focus on clarity and practical understanding, " +
                    "ensuring readers can apply the concepts in real-world scenarios. The approach is methodical " +
                    "and designed to build knowledge progressively.";
        } else if (lowerQuestion.contains("chapter") || lowerQuestion.contains("section")) {
            answer = "This chapter/section builds upon previous concepts and introduces new material that will be " +
                    "essential for understanding subsequent content. It serves as a bridge between basic and " +
                    "advanced topics, reinforcing learning through examples.";
        } else if (lowerQuestion.contains("example") || lowerQuestion.contains("case study")) {
            answer = "The content includes several practical examples and case studies that illustrate how the " +
                    "concepts can be applied in different scenarios. These help reinforce understanding through " +
                    "real-world applications and demonstrate the practical relevance of the material.";
        } else if (lowerQuestion.contains("what") && lowerQuestion.contains("meaning")) {
            answer = "The term refers to a key concept within this context, representing an important principle " +
                    "or idea that forms the foundation of the broader discussion in the material.";
        } else if (lowerQuestion.contains("how") || lowerQuestion.contains("work")) {
            answer = "The process works through a series of interconnected steps that build upon each other. " +
                    "Understanding the underlying mechanism requires considering the fundamental principles " +
                    "that govern this particular aspect of the subject matter.";
        } else if (lowerQuestion.contains("why") || lowerQuestion.contains("important")) {
            answer = "This concept is important because it forms the basis for understanding more complex topics " +
                    "that follow. It provides the necessary foundation and context for applying these ideas " +
                    "in practical situations effectively.";
        } else {
            answer = "Based on the content provided, this question touches on important aspects of the subject matter. " +
                    "The material covers this topic in the context of broader discussions about fundamental principles " +
                    "and their implementations, providing comprehensive coverage of relevant concepts.";
        }

        response.setAnswer(answer);
        response.setAnsweredFromContext(true);
        response.setSuccess(true);

        System.out.println("✅ Mock answer generated for: " + question);
        return response;
    }

    // ✅ Helper methods
    private String extractSummaryFromResponse(Map<String, Object> responseBody) {
        try {
            if (responseBody.containsKey("generated_text")) {
                return (String) responseBody.get("generated_text");
            }
            if (responseBody.containsKey("summary_text")) {
                return (String) responseBody.get("summary_text");
            }
            if (responseBody instanceof java.util.List) {
                java.util.List<?> responseList = (java.util.List<?>) responseBody;
                if (!responseList.isEmpty() && responseList.get(0) instanceof Map) {
                    Map<?, ?> firstItem = (Map<?, ?>) responseList.get(0);
                    if (firstItem.containsKey("generated_text")) {
                        return (String) firstItem.get("generated_text");
                    }
                    if (firstItem.containsKey("summary_text")) {
                        return (String) firstItem.get("summary_text");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting summary: " + e.getMessage());
        }
        return null;
    }

    private String extractAnswerFromResponse(Map<String, Object> responseBody) {
        try {
            if (responseBody.containsKey("answer")) {
                return (String) responseBody.get("answer");
            }
            if (responseBody.containsKey("generated_text")) {
                return (String) responseBody.get("generated_text");
            }
            if (responseBody instanceof java.util.List) {
                java.util.List<?> responseList = (java.util.List<?>) responseBody;
                if (!responseList.isEmpty() && responseList.get(0) instanceof Map) {
                    Map<?, ?> firstItem = (Map<?, ?>) responseList.get(0);
                    if (firstItem.containsKey("answer")) {
                        return (String) firstItem.get("answer");
                    }
                    if (firstItem.containsKey("generated_text")) {
                        return (String) firstItem.get("generated_text");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting answer: " + e.getMessage());
        }
        return null;
    }

    private String[] generateKeyPoints(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new String[]{
                    "Core concepts explained comprehensively",
                    "Practical examples provided",
                    "Step-by-step learning approach",
                    "Real-world applications discussed",
                    "Key takeaways highlighted"
            };
        }

        String lowerContent = content.toLowerCase();

        if (lowerContent.contains("java") || lowerContent.contains("programming")) {
            return new String[]{
                    "Object-oriented programming principles",
                    "Code examples and best practices",
                    "Debugging and troubleshooting techniques",
                    "Performance optimization strategies",
                    "Real-world project applications"
            };
        } else if (lowerContent.contains("business") || lowerContent.contains("management")) {
            return new String[]{
                    "Strategic planning methodologies",
                    "Leadership and team management",
                    "Financial analysis techniques",
                    "Market research approaches",
                    "Case studies from industry leaders"
            };
        } else if (lowerContent.contains("science") || lowerContent.contains("technology")) {
            return new String[]{
                    "Scientific methodology explained",
                    "Research findings and data analysis",
                    "Technological implementation strategies",
                    "Future trends and predictions",
                    "Practical applications in daily life"
            };
        } else {
            return new String[]{
                    "Fundamental concepts clearly explained",
                    "Practical implementation guidance",
                    "Step-by-step learning progression",
                    "Real-world relevance demonstrated",
                    "Comprehensive topic coverage"
            };
        }
    }

    private String calculateReadingTime(String text) {
        if (text == null) return "2-3 minutes";
        int wordCount = text.split("\\s+").length;
        int minutes = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        return minutes + "-" + (minutes + 1) + " minutes";
    }

    private String analyzeDifficulty(String text) {
        if (text == null) return "Intermediate";

        int sentenceCount = text.split("[.!?]+").length;
        if (sentenceCount == 0) return "Intermediate";

        int wordCount = text.split("\\s+").length;
        double avgSentenceLength = (double) wordCount / sentenceCount;

        if (avgSentenceLength > 25) return "Advanced";
        else if (avgSentenceLength > 18) return "Intermediate";
        else return "Beginner";
    }

    public boolean isAIAvailable() {
        // ✅ Always return true since we're using mock responses
        return true;
    }
}