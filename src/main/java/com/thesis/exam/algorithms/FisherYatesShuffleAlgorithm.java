package com.thesis.exam.algorithms;

import com.thesis.exam.model.Question;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Fisher-Yates Shuffle Algorithm
 * Used to randomize questions and answer choices to prevent cheating
 * 
 * Reference: https://rspsciencehub.com/index.php/journal/article/view/834
 */
@Component
public class FisherYatesShuffleAlgorithm {
    
    private final Random random = new Random();
    
    /**
     * Shuffles a list of questions using Fisher-Yates algorithm
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public <T> List<T> shuffle(List<T> items) {
        List<T> shuffled = new ArrayList<>(items);
        
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap elements at i and j
            T temp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, temp);
        }
        
        return shuffled;
    }
    
    /**
     * Shuffles answer options for a multiple choice question
     * Returns a map with shuffled options and the new correct answer position
     */
    public Map<String, Object> shuffleAnswerOptions(Question question) {
        List<String> options = new ArrayList<>();
        String correctAnswer = question.getCorrectAnswer();
        
        if (question.getOptionA() != null) options.add(question.getOptionA());
        if (question.getOptionB() != null) options.add(question.getOptionB());
        if (question.getOptionC() != null) options.add(question.getOptionC());
        if (question.getOptionD() != null) options.add(question.getOptionD());
        
        // Find correct answer position before shuffle
        int correctIndex = -1;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equals(correctAnswer)) {
                correctIndex = i;
                break;
            }
        }
        
        // Shuffle options
        List<String> shuffled = shuffle(options);
        
        // Find new correct answer position
        int newCorrectIndex = -1;
        for (int i = 0; i < shuffled.size(); i++) {
            if (shuffled.get(i).equals(correctAnswer)) {
                newCorrectIndex = i;
                break;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("options", shuffled);
        result.put("correctIndex", newCorrectIndex);
        result.put("correctAnswer", correctAnswer);
        
        return result;
    }
}
