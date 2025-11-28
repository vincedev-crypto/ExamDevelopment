package com.thesis.exam.model;

/**
 * Bloom's Taxonomy Levels for cognitive complexity
 * Used in IRT model for question categorization
 */
public enum BloomLevel {
    REMEMBER(1),    // Recall facts and basic concepts
    UNDERSTAND(2),  // Explain ideas or concepts
    APPLY(3),       // Use information in new situations
    ANALYZE(4),     // Draw connections among ideas
    EVALUATE(5),    // Justify a stand or decision
    CREATE(6);      // Produce new or original work
    
    private final int level;
    
    BloomLevel(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}
