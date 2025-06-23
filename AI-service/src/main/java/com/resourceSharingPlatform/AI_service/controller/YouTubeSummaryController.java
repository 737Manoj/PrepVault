//package com.resourceSharingPlatform.AI_service.service;
//
//import com.resourceSharingPlatform.AI_service.client.GeminiClient;
//import com.resourceSharingPlatform.AI_service.model.YouTubeSummary;
//import com.resourceSharingPlatform.AI_service.repository.YouTubeSummaryRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//@RequiredArgsConstructor
//public class YouTubeSummaryService {
//
//    private final YouTubeSummaryRepository repository;
//    private final GeminiClient geminiClient;
//    private final WebClient webClient = WebClient.builder().build();
//
//    @Value("${youtube.api.key}")
//    private String youtubeApiKey;
//
////    public YouTubeSummary generateSummary(String videoUrl) {
////        String videoId = extractVideoId(videoUrl);
////        if (videoId == null) {
////            return buildErrorSummary(videoUrl, "Invalid YouTube URL");
////        }
////
////        String transcript = fetchTranscriptFromYouTube(videoUrl);
////        String summary = geminiClient.generateSummary(transcript);
////
////        // Fetch video title
////        String videoTitle = fetchVideoTitle(videoId);
////
////        YouTubeSummary youTubeSummary = YouTubeSummary.builder()
////                .id(UUID.randomUUID().toString())
////                .videoUrl(videoUrl)
////                .videoTitle(videoTitle)
////                .transcript(transcript)
////                .summary(summary)
////                .build();
////
////        return repository.save(youTubeSummary);
////    }
////
////    private String fetchTranscriptFromYouTube(String videoUrl) {
////        try {
////            String videoId = extractVideoId(videoUrl);
////            if (videoId == null) {
////                return "Transcript could not be fetched: Invalid YouTube URL";
////            }
////
////            // Fetch caption tracks
////            Map captionResponse = webClient.get()
////                    .uri("https://www.googleapis.com/youtube/v3/captions?part=snippet&videoId=" + videoId + "&key=" + youtubeApiKey)
////                    .retrieve()
////                    .bodyToMono(Map.class)
////                    .block();
////
////            List<Map> items = (List<Map>) captionResponse.get("items");
////            if (items == null || items.isEmpty()) {
////                return fetchDescriptionFallback(videoId);
////            }
////
////            // Find suitable caption track (prefer manual, English)
////            String captionId = null;
////            for (Map item : items) {
////                Map snippet = (Map) item.get("snippet");
////                String trackKind = (String) snippet.get("trackKind");
////                String language = (String) snippet.get("language");
////                if ("standard".equals(trackKind) && language.startsWith("en")) {
////                    captionId = (String) item.get("id");
////                    break;
////                }
////            }
////            if (captionId == null) {
////                // Fallback to first track if no standard English
////                captionId = (String) items.get(0).get("id");
////            }
////            if (captionId == null) {
////                return fetchDescriptionFallback(videoId);
////            }
////
////            // Fetch transcript
////            String transcript = webClient.get()
////                    .uri("https://www.googleapis.com/youtube/v3/captions/" + captionId + "?tfmt=srt&key=" + youtubeApiKey)
////                    .retrieve()
////                    .bodyToMono(String.class)
////                    .block();
////
////            return cleanSrtTranscript(transcript);
////
////        } catch (Exception e) {
////            if (e.getMessage().contains("401")) {
////                return fetchDescriptionFallback(extractVideoId(videoUrl));
////            }
////            return "Transcript could not be fetched: " + e.getMessage();
////        }
////    }
////
////    private String fetchVideoTitle(String videoId) {
////        try {
////            Map videoResponse = webClient.get()
////                    .uri("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + youtubeApiKey)
////                    .retrieve()
////                    .bodyToMono(Map.class)
////                    .block();
////
////            List<Map> items = (List<Map>) videoResponse.get("items");
////            if (items != null && !items.isEmpty()) {
////                Map snippet = (Map) items.get(0).get("snippet");
////                return (String) snippet.get("title");
////            }
////            return null;
////        } catch (Exception e) {
////            return null;
////        }
////    }
////
////    private String extractVideoId(String videoUrl) {
////        Pattern pattern = Pattern.compile("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([^&?]+)");
////        Matcher matcher = pattern.matcher(videoUrl);
////        return matcher.find() ? matcher.group(1) : null;
////    }
////
////    private String fetchDescriptionFallback(String videoId) {
////        try {
////            Map videoResponse = webClient.get()
////                    .uri("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + youtubeApiKey)
////                    .retrieve()
////                    .bodyToMono(Map.class)
////                    .block();
////
////            List<Map> items = (List<Map>) videoResponse.get("items");
////            if (items != null && !items.isEmpty()) {
////                Map snippet = (Map) items.get(0).get("snippet");
////                return (String) snippet.get("description");
////            }
////            return "No transcript or description available";
////        } catch (Exception e) {
////            return "No transcript or description available: " + e.getMessage();
////        }
////    }
////
////    private String cleanSrtTranscript(String srt) {
////        if (srt == null) {
////            return "";
////        }
////        return srt.replaceAll("\\d+\\n\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}\\n", "")
////                .replaceAll("\\n\\d+\\n", "\n")
////                .replaceAll("\\n+", " ")
////                .trim();
////    }
////
////    private YouTubeSummary buildErrorSummary(String videoUrl, String errorMessage) {
////        return YouTubeSummary.builder()
////                .id(UUID.randomUUID().toString())
////                .videoUrl(videoUrl)
////                .transcript(errorMessage)
////                .summary("Summary unavailable: " + errorMessage)
////                .build();
////    }
////}
