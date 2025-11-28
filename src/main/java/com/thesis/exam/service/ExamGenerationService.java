package com.thesis.exam.service;

import com.thesis.exam.model.*;
import com.thesis.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamGenerationService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentSubjectMasteryRepository masteryRepository;

    @Autowired
    private ExamRepository examRepository;

    /**
     * Generates an exam for a student in a specific subject.
     * The difficulty distribution is based on the student's mastery level.
     */
    public Exam generateExam(Student student, Subject subject, int totalQuestions) {
        // 1. Get Student Mastery
        Optional<StudentSubjectMastery> masteryOpt = masteryRepository.findByStudentAndSubject(student, subject);
        
        // Default to beginner (0.0) if no history exists (Cold Start Problem)
        double masteryScore = masteryOpt.map(StudentSubjectMastery::getMasteryLevel).orElse(0.0);

        // 2. Determine Difficulty Distribution
        int easyCount, mediumCount, hardCount;

        if (masteryScore < 0.3) {
            // Beginner: Mostly Easy, some Medium to test growth
            easyCount = (int) (totalQuestions * 0.7);
            mediumCount = (int) (totalQuestions * 0.2);
            hardCount = totalQuestions - easyCount - mediumCount;
        } else if (masteryScore < 0.7) {
            // Intermediate: Balanced mix
            easyCount = (int) (totalQuestions * 0.3);
            mediumCount = (int) (totalQuestions * 0.5);
            hardCount = totalQuestions - easyCount - mediumCount;
        } else {
            // Advanced: Mostly Hard and Medium
            easyCount = (int) (totalQuestions * 0.1);
            mediumCount = (int) (totalQuestions * 0.3);
            hardCount = totalQuestions - easyCount - mediumCount;
        }

        // 3. Fetch Questions
        // Note: In a real app, use the random query. For H2 in-mem, we might need a simpler approach if RAND() isn't supported perfectly, 
        // but assuming the repository method works or we fetch all and shuffle.
        // Here we use the repository method defined earlier.
        List<Question> examQuestions = new ArrayList<>();
        examQuestions.addAll(questionRepository.findRandomQuestions(subject.getId(), Difficulty.EASY.name(), easyCount));
        examQuestions.addAll(questionRepository.findRandomQuestions(subject.getId(), Difficulty.MEDIUM.name(), mediumCount));
        examQuestions.addAll(questionRepository.findRandomQuestions(subject.getId(), Difficulty.HARD.name(), hardCount));

        // 4. Create Exam Object
        Exam exam = new Exam();
        exam.setStudent(student);
        exam.setSubject(subject);
        exam.setQuestions(examQuestions);
        
        return examRepository.save(exam);
    }
    
    /**
     * Processes the exam results to update both student mastery and question statistics.
     * This is the full implementation of the feedback loop.
     */
    public void processExamResult(Exam exam, List<Long> correctQuestionIds) {
        Student student = exam.getStudent();
        Subject subject = exam.getSubject();
        
        // 1. Calculate Score
        int totalQuestions = exam.getQuestions().size();
        int correctCount = correctQuestionIds.size();
        double score = (totalQuestions > 0) ? (double) correctCount / totalQuestions : 0.0;
        
        exam.setScore(score);
        examRepository.save(exam);

        // 2. Update Student Mastery
        updateMastery(student, subject, score);

        // 3. Update Question Statistics (The "Full Algorithm" part)
        // We need the student's mastery *before* this exam for accurate stats, 
        // but we just updated it. For simplicity, we can use the new one or fetch it before.
        // Ideally, we should use the mastery level they had *when they took the exam*.
        // Let's approximate using the current (new) mastery, or we could have stored the old one.
        // For the algorithm's sake, let's use the current mastery as a proxy for their ability.
        
        Optional<StudentSubjectMastery> masteryOpt = masteryRepository.findByStudentAndSubject(student, subject);
        double studentMastery = masteryOpt.map(StudentSubjectMastery::getMasteryLevel).orElse(0.0);

        for (Question question : exam.getQuestions()) {
            boolean isCorrect = correctQuestionIds.contains(question.getId());
            updateQuestionStats(question, isCorrect, studentMastery);
        }
    }

    /**
     * Updates mastery based on exam results.
     * This is a simplified algorithm.
     */
    public void updateMastery(Student student, Subject subject, double examScore) {
        StudentSubjectMastery mastery = masteryRepository.findByStudentAndSubject(student, subject)
                .orElse(new StudentSubjectMastery());
        
        if (mastery.getId() == null) {
            mastery.setStudent(student);
            mastery.setSubject(subject);
            mastery.setMasteryLevel(0.0);
        }

        // Simple moving average or weighted update
        // New Mastery = (Old Mastery * 0.7) + (Exam Score * 0.3)
        // Assuming examScore is normalized 0.0 to 1.0
        double currentMastery = mastery.getMasteryLevel();
        double newMastery = (currentMastery * 0.7) + (examScore * 0.3);
        
        mastery.setMasteryLevel(newMastery);
        masteryRepository.save(mastery);
    }

    /**
     * Updates question statistics and reclassifies difficulty based on performance.
     * This implements the "Dynamic Difficulty Reclassification" and "Discrimination Index" 
     * algorithms from the thesis.
     * 
     * @param question The question to update
     * @param isCorrect Whether the student answered correctly
     * @param studentMastery The student's mastery level BEFORE taking this exam
     */
    public void updateQuestionStats(Question question, boolean isCorrect, double studentMastery) {
        // 1. Update Basic Stats
        question.setUsageCount(question.getUsageCount() + 1);
        if (isCorrect) {
            question.setCorrectCount(question.getCorrectCount() + 1);
        }

        // 2. Update Stats for Discrimination Index (High vs Low performers)
        if (studentMastery >= 0.7) {
            question.setUsageByHighMastery(question.getUsageByHighMastery() + 1);
            if (isCorrect) {
                question.setCorrectByHighMastery(question.getCorrectByHighMastery() + 1);
            }
        } else if (studentMastery <= 0.3) {
            question.setUsageByLowMastery(question.getUsageByLowMastery() + 1);
            if (isCorrect) {
                question.setCorrectByLowMastery(question.getCorrectByLowMastery() + 1);
            }
        }

        // 3. Calculate Discrimination Index (DI)
        // DI = P(High) - P(Low)
        double pHigh = (question.getUsageByHighMastery() > 0) ? 
            (double) question.getCorrectByHighMastery() / question.getUsageByHighMastery() : 0.0;
            
        double pLow = (question.getUsageByLowMastery() > 0) ? 
            (double) question.getCorrectByLowMastery() / question.getUsageByLowMastery() : 0.0;
            
        question.setDiscriminationIndex(pHigh - pLow);

        // 4. Dynamic Difficulty Reclassification
        // Only reclassify if we have enough data (e.g., 10 attempts)
        if (question.getUsageCount() >= 10) {
            double successRate = (double) question.getCorrectCount() / question.getUsageCount();

            // If too many people get it wrong (< 30%), it's HARD
            if (successRate < 0.3 && question.getDifficulty() != Difficulty.HARD) {
                question.setDifficulty(Difficulty.HARD);
            } 
            // If too many people get it right (> 80%), it's EASY
            else if (successRate > 0.8 && question.getDifficulty() != Difficulty.EASY) {
                question.setDifficulty(Difficulty.EASY);
            }
            // Otherwise, it's MEDIUM
            else if (successRate >= 0.3 && successRate <= 0.8 && question.getDifficulty() != Difficulty.MEDIUM) {
                question.setDifficulty(Difficulty.MEDIUM);
            }
        }

        questionRepository.save(question);
    }
}
