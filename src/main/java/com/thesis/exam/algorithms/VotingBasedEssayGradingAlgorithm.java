package com.thesis.exam.algorithms;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Voting-Based Unsupervised Grading Algorithm for Essay Questions
 * Uses NLP techniques to grade essays without pre-labeled training data
 * 
 * Reference: https://ir.lib.nycu.edu.tw/bitstream/11536/32252/1/000282844200012.pdf
 */
@Component
public class VotingBasedEssayGradingAlgorithm {
    
    /**
     * Grade an essay based on multiple criteria using voting mechanism
     * 
     * @param essayText The student's essay answer
     * @param referenceAnswer The model/reference answer
     * @param keywords Important keywords that should appear
     * @return Grade score (0-1)
     */
    public double gradeEssay(String essayText, String referenceAnswer, 
                            List<String> keywords) {
        if (essayText == null || essayText.trim().isEmpty()) {
            return 0.0;
        }
        
        // Multiple grading criteria (voters)
        double keywordScore = evaluateKeywordPresence(essayText, keywords);
        double lengthScore = evaluateLength(essayText, referenceAnswer);
        double vocabularyScore = evaluateVocabulary(essayText);
        double coherenceScore = evaluateCoherence(essayText);
        double similarityScore = evaluateSimilarity(essayText, referenceAnswer);
        
        // Voting weights
        double w1 = 0.30;  // Keyword presence (most important)
        double w2 = 0.15;  // Length appropriateness
        double w3 = 0.15;  // Vocabulary richness
        double w4 = 0.20;  // Coherence
        double w5 = 0.20;  // Similarity to reference
        
        // Weighted voting
        double finalScore = (keywordScore * w1) + 
                          (lengthScore * w2) + 
                          (vocabularyScore * w3) + 
                          (coherenceScore * w4) + 
                          (similarityScore * w5);
        
        return Math.max(0.0, Math.min(1.0, finalScore));
    }
    
    /**
     * Evaluate presence of important keywords
     */
    private double evaluateKeywordPresence(String essay, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return 1.0;
        }
        
        String essayLower = essay.toLowerCase();
        int foundCount = 0;
        
        for (String keyword : keywords) {
            if (essayLower.contains(keyword.toLowerCase())) {
                foundCount++;
            }
        }
        
        return (double) foundCount / keywords.size();
    }
    
    /**
     * Evaluate if essay length is appropriate
     */
    private double evaluateLength(String essay, String reference) {
        int essayWords = countWords(essay);
        int refWords = countWords(reference);
        
        if (refWords == 0) return 1.0;
        
        double ratio = (double) essayWords / refWords;
        
        // Ideal: 0.7 to 1.5 times reference length
        if (ratio >= 0.7 && ratio <= 1.5) {
            return 1.0;
        } else if (ratio >= 0.5 && ratio <= 2.0) {
            return 0.7;
        } else if (ratio >= 0.3 && ratio <= 3.0) {
            return 0.4;
        } else {
            return 0.2;
        }
    }
    
    /**
     * Evaluate vocabulary richness (unique words ratio)
     */
    private double evaluateVocabulary(String essay) {
        String[] words = essay.toLowerCase().split("\\s+");
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        
        if (words.length == 0) return 0.0;
        
        double uniqueRatio = (double) uniqueWords.size() / words.length;
        
        // Normalize: typically 0.4-0.8 for good essays
        return Math.min(1.0, uniqueRatio / 0.7);
    }
    
    /**
     * Evaluate coherence based on sentence structure
     */
    private double evaluateCoherence(String essay) {
        String[] sentences = essay.split("[.!?]+");
        
        if (sentences.length < 2) {
            return 0.5;  // Too short to evaluate
        }
        
        // Check for transition words
        String[] transitions = {"however", "therefore", "moreover", "furthermore",
                              "additionally", "consequently", "thus", "hence",
                              "first", "second", "finally", "in conclusion"};
        
        int transitionCount = 0;
        String essayLower = essay.toLowerCase();
        
        for (String transition : transitions) {
            if (essayLower.contains(transition)) {
                transitionCount++;
            }
        }
        
        // Good coherence: at least 1 transition per 3 sentences
        double expectedTransitions = sentences.length / 3.0;
        double coherenceRatio = Math.min(1.0, transitionCount / expectedTransitions);
        
        return coherenceRatio;
    }
    
    /**
     * Evaluate similarity to reference answer using Jaccard similarity
     */
    private double evaluateSimilarity(String essay, String reference) {
        Set<String> essayWords = new HashSet<>(
            Arrays.asList(essay.toLowerCase().split("\\s+"))
        );
        Set<String> refWords = new HashSet<>(
            Arrays.asList(reference.toLowerCase().split("\\s+"))
        );
        
        // Remove common stop words
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "but", "is", "are", "was", "were",
            "in", "on", "at", "to", "for", "of", "with", "by"
        ));
        
        essayWords.removeAll(stopWords);
        refWords.removeAll(stopWords);
        
        // Jaccard similarity
        Set<String> intersection = new HashSet<>(essayWords);
        intersection.retainAll(refWords);
        
        Set<String> union = new HashSet<>(essayWords);
        union.addAll(refWords);
        
        if (union.isEmpty()) return 0.0;
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Count words in text
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
    
    /**
     * Provide detailed feedback for essay
     */
    public Map<String, Object> generateFeedback(String essay, String reference,
                                               List<String> keywords) {
        Map<String, Object> feedback = new HashMap<>();
        
        double keywordScore = evaluateKeywordPresence(essay, keywords);
        double lengthScore = evaluateLength(essay, reference);
        double vocabularyScore = evaluateVocabulary(essay);
        double coherenceScore = evaluateCoherence(essay);
        double similarityScore = evaluateSimilarity(essay, reference);
        
        feedback.put("overallScore", gradeEssay(essay, reference, keywords));
        feedback.put("keywordCoverage", keywordScore);
        feedback.put("lengthAppropriate", lengthScore);
        feedback.put("vocabularyRichness", vocabularyScore);
        feedback.put("coherence", coherenceScore);
        feedback.put("contentSimilarity", similarityScore);
        feedback.put("wordCount", countWords(essay));
        
        // Generate suggestions
        List<String> suggestions = new ArrayList<>();
        if (keywordScore < 0.5) {
            suggestions.add("Include more key concepts from the topic");
        }
        if (lengthScore < 0.5) {
            suggestions.add("Adjust essay length to be more comprehensive");
        }
        if (vocabularyScore < 0.5) {
            suggestions.add("Use more varied vocabulary");
        }
        if (coherenceScore < 0.5) {
            suggestions.add("Add transition words to improve flow");
        }
        if (similarityScore < 0.3) {
            suggestions.add("Ensure answer addresses the question properly");
        }
        
        feedback.put("suggestions", suggestions);
        
        return feedback;
    }
}
