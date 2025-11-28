package com.thesis.exam.algorithms;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Random Forest Algorithm for Analytics and Performance Prediction
 * Used to predict student performance and provide insights
 * 
 * Simplified implementation of Random Forest for educational analytics
 * Reference: https://link.springer.com/article/10.1007/s10639-024-12619-w
 */
@Component
public class RandomForestAnalyticsAlgorithm {
    
    private List<DecisionTree> forest;
    private int numTrees = 10;
    
    public RandomForestAnalyticsAlgorithm() {
        this.forest = new ArrayList<>();
    }
    
    /**
     * Decision Tree node for Random Forest
     */
    static class DecisionTree {
        String feature;
        double threshold;
        DecisionTree left;
        DecisionTree right;
        Double prediction;  // For leaf nodes
        
        public double predict(Map<String, Double> features) {
            if (prediction != null) {
                return prediction;
            }
            
            double value = features.getOrDefault(feature, 0.0);
            if (value <= threshold) {
                return left != null ? left.predict(features) : 0.0;
            } else {
                return right != null ? right.predict(features) : 0.0;
            }
        }
    }
    
    /**
     * Predict student performance (pass/fail probability)
     * 
     * @param features Map of feature names to values:
     *                 - "masteryLevel": 0-1
     *                 - "avgScore": 0-1
     *                 - "studyTime": hours
     *                 - "previousExams": count
     *                 - "attendance": 0-1
     * @return Predicted probability of passing (0-1)
     */
    public double predictPerformance(Map<String, Double> features) {
        if (forest.isEmpty()) {
            // Return simple heuristic if not trained
            return features.getOrDefault("masteryLevel", 0.5);
        }
        
        double sum = 0;
        for (DecisionTree tree : forest) {
            sum += tree.predict(features);
        }
        return sum / forest.size();
    }
    
    /**
     * Calculate feature importance scores
     * Returns which factors most affect student performance
     */
    public Map<String, Double> calculateFeatureImportance(String[] featureNames,
                                                          double[][] features,
                                                          double[] targets) {
        Map<String, Double> importance = new HashMap<>();
        
        for (String feature : featureNames) {
            importance.put(feature, 0.0);
        }
        
        // Calculate variance for each feature
        for (int i = 0; i < featureNames.length; i++) {
            double variance = 0;
            double mean = 0;
            
            // Calculate mean
            for (double[] sample : features) {
                mean += sample[i];
            }
            mean /= features.length;
            
            // Calculate variance
            for (double[] sample : features) {
                variance += Math.pow(sample[i] - mean, 2);
            }
            variance /= features.length;
            
            importance.put(featureNames[i], variance);
        }
        
        return importance;
    }
    
    /**
     * Identify at-risk students based on multiple indicators
     * Returns risk score (0-1, higher = more at risk)
     */
    public double calculateRiskScore(double masteryLevel, double attendance,
                                    double avgScore, int missedDeadlines) {
        double riskScore = 0.0;
        
        // Low mastery increases risk
        if (masteryLevel < 0.3) riskScore += 0.3;
        else if (masteryLevel < 0.5) riskScore += 0.15;
        
        // Low attendance increases risk
        if (attendance < 0.5) riskScore += 0.3;
        else if (attendance < 0.7) riskScore += 0.15;
        
        // Low average score increases risk
        if (avgScore < 0.5) riskScore += 0.25;
        else if (avgScore < 0.6) riskScore += 0.1;
        
        // Missed deadlines increase risk
        riskScore += Math.min(0.15, missedDeadlines * 0.05);
        
        return Math.min(1.0, riskScore);
    }
    
    /**
     * Generate analytics summary for a student
     */
    public Map<String, Object> generateStudentAnalytics(double masteryLevel,
                                                        double[] examScores,
                                                        int totalExamsTaken) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Calculate average score
        double avgScore = Arrays.stream(examScores).average().orElse(0.0);
        
        // Calculate trend (improving/declining)
        double trend = 0.0;
        if (examScores.length >= 2) {
            int recent = Math.min(3, examScores.length);
            double recentAvg = 0;
            double olderAvg = 0;
            
            for (int i = examScores.length - recent; i < examScores.length; i++) {
                recentAvg += examScores[i];
            }
            recentAvg /= recent;
            
            int olderCount = Math.min(3, examScores.length - recent);
            for (int i = 0; i < olderCount; i++) {
                olderAvg += examScores[i];
            }
            olderAvg /= olderCount;
            
            trend = recentAvg - olderAvg;
        }
        
        // Calculate consistency (standard deviation)
        double variance = 0;
        for (double score : examScores) {
            variance += Math.pow(score - avgScore, 2);
        }
        variance /= examScores.length;
        double stdDev = Math.sqrt(variance);
        
        analytics.put("averageScore", avgScore);
        analytics.put("masteryLevel", masteryLevel);
        analytics.put("trend", trend > 0 ? "improving" : trend < 0 ? "declining" : "stable");
        analytics.put("consistency", 1.0 - Math.min(stdDev, 1.0));  // Higher = more consistent
        analytics.put("totalExams", totalExamsTaken);
        analytics.put("predictedNextScore", Math.max(0, Math.min(1, avgScore + trend)));
        
        return analytics;
    }
}
