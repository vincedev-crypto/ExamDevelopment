package com.thesis.exam.algorithms;

import org.springframework.stereotype.Component;

/**
 * Item Response Theory (IRT) with Bloom's Taxonomy Integration
 * 
 * IRT 3-Parameter Logistic Model (3PL):
 * P(θ) = c + (1-c) / (1 + e^(-a(θ - b)))
 * 
 * Where:
 * - θ (theta) = student ability
 * - a = discrimination parameter
 * - b = difficulty parameter
 * - c = guessing parameter
 * 
 * Bloom's Taxonomy Integration:
 * - REMEMBER/UNDERSTAND: Lower difficulty (b = -1 to 0)
 * - APPLY/ANALYZE: Medium difficulty (b = 0 to 1)
 * - EVALUATE/CREATE: Higher difficulty (b = 1 to 2)
 * 
 * Reference: https://www.jisem-journal.com/index.php/journal/article/view/4482
 */
@Component
public class IRTBloomTaxonomyAlgorithm {
    
    /**
     * Calculate probability of correct answer using 3PL IRT model
     * 
     * @param theta Student ability (-3 to +3)
     * @param a Discrimination parameter (0.5 to 2.5)
     * @param b Difficulty parameter (-3 to +3)
     * @param c Guessing parameter (0 to 0.5, typically 0.25 for 4-option MC)
     * @return Probability of correct response (0 to 1)
     */
    public double calculateProbability(double theta, double a, double b, double c) {
        double exponent = -a * (theta - b);
        return c + ((1 - c) / (1 + Math.exp(exponent)));
    }
    
    /**
     * Estimate student ability (theta) using Maximum Likelihood Estimation
     * Simplified version using Newton-Raphson method
     * 
     * @param responses Array of 1 (correct) or 0 (incorrect)
     * @param difficulties Array of item difficulty parameters
     * @param discriminations Array of item discrimination parameters
     * @param guessings Array of item guessing parameters
     * @return Estimated theta (ability)
     */
    public double estimateAbility(int[] responses, double[] difficulties, 
                                  double[] discriminations, double[] guessings) {
        double theta = 0.0; // Initial estimate
        int maxIterations = 20;
        double tolerance = 0.001;
        
        for (int iter = 0; iter < maxIterations; iter++) {
            double firstDerivative = 0.0;
            double secondDerivative = 0.0;
            
            for (int i = 0; i < responses.length; i++) {
                double a = discriminations[i];
                double b = difficulties[i];
                double c = guessings[i];
                
                double p = calculateProbability(theta, a, b, c);
                double q = 1 - p;
                
                // First derivative (information)
                double pStar = (p - c) / (1 - c);
                double info = a * a * pStar * (1 - pStar) / (p * q);
                firstDerivative += a * (responses[i] - p) / (p * q);
                secondDerivative -= info;
            }
            
            // Newton-Raphson update
            double delta = firstDerivative / secondDerivative;
            theta = theta - delta;
            
            // Check convergence
            if (Math.abs(delta) < tolerance) {
                break;
            }
        }
        
        // Constrain theta to reasonable range
        return Math.max(-3, Math.min(3, theta));
    }
    
    /**
     * Calculate information function for an item
     * Higher information = more precise ability estimation
     */
    public double calculateInformation(double theta, double a, double b, double c) {
        double p = calculateProbability(theta, a, b, c);
        double pStar = (p - c) / (1 - c);
        double q = 1 - p;
        
        return (a * a * pStar * (1 - pStar)) / (p * q);
    }
    
    /**
     * Map Bloom's Taxonomy level to difficulty range
     */
    public double[] getBloomDifficultyRange(String bloomLevel) {
        return switch (bloomLevel) {
            case "REMEMBER" -> new double[]{-2.0, -0.5};
            case "UNDERSTAND" -> new double[]{-1.0, 0.0};
            case "APPLY" -> new double[]{-0.5, 0.5};
            case "ANALYZE" -> new double[]{0.0, 1.0};
            case "EVALUATE" -> new double[]{0.5, 1.5};
            case "CREATE" -> new double[]{1.0, 2.5};
            default -> new double[]{-1.0, 1.0};
        };
    }
    
    /**
     * Select next best question using Maximum Information criterion
     * This is used in Computerized Adaptive Testing (CAT)
     */
    public int selectNextQuestion(double currentTheta, double[] difficulties,
                                  double[] discriminations, double[] guessings,
                                  boolean[] alreadyAsked) {
        double maxInfo = -1;
        int bestQuestion = -1;
        
        for (int i = 0; i < difficulties.length; i++) {
            if (!alreadyAsked[i]) {
                double info = calculateInformation(currentTheta, discriminations[i],
                                                   difficulties[i], guessings[i]);
                if (info > maxInfo) {
                    maxInfo = info;
                    bestQuestion = i;
                }
            }
        }
        
        return bestQuestion;
    }
}
