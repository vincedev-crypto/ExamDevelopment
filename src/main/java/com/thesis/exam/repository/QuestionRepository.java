package com.thesis.exam.repository;

import com.thesis.exam.model.Question;
import com.thesis.exam.model.Difficulty;
import com.thesis.exam.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Fetch questions by subject and difficulty
    List<Question> findBySubjectAndDifficulty(Subject subject, Difficulty difficulty);

    // Fetch all questions for a subject
    List<Question> findBySubjectId(Long subjectId);

    // Helper to get random questions (naive implementation for prototype)
    @Query(value = "SELECT * FROM question q WHERE q.subject_id = :subjectId AND q.difficulty = :difficulty ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("subjectId") Long subjectId, @Param("difficulty") String difficulty, @Param("limit") int limit);
}
