package com.paulotech.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulotech.aiservice.entity.Activity;
import com.paulotech.aiservice.entity.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response: {}", aiResponse);
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidate")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
            String jsonContent = textNode.asText()
                    .replaceAll("```json", "")
                    .replaceAll("\\n```", "")
                    .trim();
            //log.info("PARSED RESPONSE FROM AI: {}", jsonContent);
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall Analysis: ");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace Analysis: ");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate Analysis: ");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned Analysis: ");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safity = extractSafetyGuidelines(analysisJson.path("safity"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safity)
                    .createdAt(LocalDateTime.now())
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate recommendation due to an error.")
                .improvements(Collections.singletonList("No specific improvements suggested."))
                .suggestions(Collections.singletonList("No specific suggestions provided."))
                .safety(Arrays.asList(
                        "Always warn up before starting any physical activity.",
                        "Stay hydrated during the activity.",
                        "Listen to your body and stop if you feel any pain or discomfort."
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safityNode){
        List<String> safity = new ArrayList<>();
        if(safityNode.isArray()) {
            safityNode.forEach(item -> safity.add(item.asText()));
        }
        return safity.isEmpty() ?
                Collections.singletonList("No specific safety guidelines provided.") :
                safity;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode){
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("area").asText();
                String description = suggestion.path("recommendation").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions provided.") :
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode){
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements suggested.") :
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format(
                """
                 You are an AI assistant this fitness activity and provide detailed recommendations in the following .
                 {
                 "analysis": {
                     "overall": "Provide a brief summary of the activity.",
                     "pace": "Describe the pace of the activity.",
                     "heartRate": "Analyze the heart rate data and provide insights.",
                     "caloriesBurned": "Estimate the calories burned during the activity.",
                 },
                 "improvements": [
                     {
                            "area": "Identify an area for improvement.",
                            "recommendation": "Provide a specific recommendation for improvement."
                     }
                 ],
                 "suggestions": [
                    {
                    "workout": "Workout name",
                    "description": "Brief description of the workout.",
                    }
                 ],
                 "safety": [
                    "Safety point 1",
                    "Safety point 2",
                 ]
                 }
                 Analyze this activity:
                    Activity Type: %s
                    Duration: %s minutes
                    Calories Burned: %s
                    Additional Metrics: %s
                    
                 Provide detailed analysis focusing on performance, improvements, and safety recommendations.
                 Ensure the response is structured in JSON format with clear sections for analysis, improvements, suggestions, and safety.
                 """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
