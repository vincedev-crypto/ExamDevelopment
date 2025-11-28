package com.thesis.exam.algorithms;

import org.springframework.stereotype.Component;

/**
 * Linear Regression Algorithm for Adaptive Question Difficulty
 * Predicts optimal difficulty based on student performance patterns
 * 
 * Formula: difficulty = β₀ + β₁(mastery) + β₂(avgResponseTime) + β₃(recentAccuracy)
 * 
 * Reference: Integrating automatic question generation with computerised adaptive test
 */
@Component
public class LinearRegressionDifficultyAlgorithm {
    
    // Coefficients learned from data (these would be trained on real data)
    private double beta0 = 0.5;  // Intercept
    private double beta1 = 0.8;  // Mastery coefficient
    private double beta2 = -0.1; // Response time coefficient (negative: faster = easier)
    private double beta3 = 0.6;  // Recent accuracy coefficient
    
    /**
     * Predict optimal difficulty level for next question
     * 
     * @param masteryLevel Current student mastery (0 to 1)
     * @param avgResponseTime Average response time in seconds
     * @param recentAccuracy Accuracy in last N questions (0 to 1)
     * @return Predicted difficulty (0 to 1)
     */
    public double predictDifficulty(double masteryLevel, double avgResponseTime, 
                                    double recentAccuracy) {
        // Normalize response time (assuming 0-300 seconds range)
        double normalizedTime = Math.min(avgResponseTime / 300.0, 1.0);
        
        // Linear regression prediction
        double difficulty = beta0 + 
                          (beta1 * masteryLevel) + 
                          (beta2 * normalizedTime) + 
                          (beta3 * recentAccuracy);
        
        // Constrain to [0, 1]
        return Math.max(0.0, Math.min(1.0, difficulty));
    }
    
    /**
     * Update coefficients using gradient descent (simple training)
     * This would be called periodically to retrain the model
     */
    public void trainModel(double[][] features, double[] actualDifficulties, 
                          double learningRate, int iterations) {
        int n = features.length;
        
        for (int iter = 0; iter < iterations; iter++) {
            double gradBeta0 = 0, gradBeta1 = 0, gradBeta2 = 0, gradBeta3 = 0;
            
            for (int i = 0; i < n; i++) {
                double mastery = features[i][0];
                double responseTime = Math.min(features[i][1] / 300.0, 1.0);
                double accuracy = features[i][2];
                
                double predicted = beta0 + (beta1 * mastery) + 
                                 (beta2 * responseTime) + (beta3 * accuracy);
                double error = predicted - actualDifficulties[i];
                
                // Calculate gradients
                gradBeta0 += error;
                gradBeta1 += error * mastery;
                gradBeta2 += error * responseTime;
                gradBeta3 += error * accuracy;
            }
            
            // Update coefficients
            beta0 -= learningRate * gradBeta0 / n;
            beta1 -= learningRate * gradBeta1 / n;
            beta2 -= learningRate * gradBeta2 / n;
            beta3 -= learningRate * gradBeta3 / n;
        }
    }
    
    /**
     * Calculate R-squared (coefficient of determination) for model evaluation
     */
    public double calculateRSquared(double[][] features, double[] actualDifficulties) {
        int n = features.length;
        double mean = 0;
        
        for (double actual : actualDifficulties) {
            mean += actual;
        }
        mean /= n;
        
        double totalSS = 0;  // Total sum of squares
        double residualSS = 0;  // Residual sum of squares
        
        for (int i = 0; i < n; i++) {
            double mastery = features[i][0];
            double responseTime = Math.min(features[i][1] / 300.0, 1.0);
            double accuracy = features[i][2];
            
            double predicted = beta0 + (beta1 * mastery) + 
                             (beta2 * responseTime) + (beta3 * accuracy);
            
            totalSS += Math.pow(actualDifficulties[i] - mean, 2);
            residualSS += Math.pow(actualDifficulties[i] - predicted, 2);
        }
        
        return 1 - (residualSS / totalSS);
    }
}
