package com.thesis.exam;

import com.thesis.exam.model.*;
import com.thesis.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create Subjects
        Subject math = new Subject();
        math.setName("Mathematics");
        math.setDescription("Algebra, Geometry, Calculus");
        subjectRepository.save(math);

        Subject science = new Subject();
        science.setName("Science");
        science.setDescription("Physics, Chemistry, Biology");
        subjectRepository.save(science);

        Subject programming = new Subject();
        programming.setName("Programming");
        programming.setDescription("Java, Python, Algorithms");
        subjectRepository.save(programming);

        // Create Students
        Student lance = new Student();
        lance.setName("Lance");
        lance.setEmail("lance@example.com");
        studentRepository.save(lance);

        Student henry = new Student();
        henry.setName("Henry");
        henry.setEmail("henry@example.com");
        studentRepository.save(henry);

        // Create Sample Questions for Math
        Question mathQ1 = new Question();
        mathQ1.setContent("What is 2 + 2?");
        mathQ1.setDifficulty(Difficulty.EASY);
        mathQ1.setSubject(math);
        mathQ1.setCorrectAnswer("4");
        questionRepository.save(mathQ1);

        Question mathQ2 = new Question();
        mathQ2.setContent("Solve for x: 2x + 5 = 15");
        mathQ2.setDifficulty(Difficulty.MEDIUM);
        mathQ2.setSubject(math);
        mathQ2.setCorrectAnswer("5");
        questionRepository.save(mathQ2);

        Question mathQ3 = new Question();
        mathQ3.setContent("Find the derivative of f(x) = x^3 + 2x^2 - 5x + 7");
        mathQ3.setDifficulty(Difficulty.HARD);
        mathQ3.setSubject(math);
        mathQ3.setCorrectAnswer("3x^2 + 4x - 5");
        questionRepository.save(mathQ3);

        // Create Sample Questions for Science
        Question sciQ1 = new Question();
        sciQ1.setContent("What is the chemical symbol for water?");
        sciQ1.setDifficulty(Difficulty.EASY);
        sciQ1.setSubject(science);
        sciQ1.setCorrectAnswer("H2O");
        questionRepository.save(sciQ1);

        Question sciQ2 = new Question();
        sciQ2.setContent("What is Newton's second law of motion?");
        sciQ2.setDifficulty(Difficulty.MEDIUM);
        sciQ2.setSubject(science);
        sciQ2.setCorrectAnswer("F = ma");
        questionRepository.save(sciQ2);

        Question sciQ3 = new Question();
        sciQ3.setContent("Explain the process of photosynthesis in detail");
        sciQ3.setDifficulty(Difficulty.HARD);
        sciQ3.setSubject(science);
        sciQ3.setCorrectAnswer("6CO2 + 6H2O + light energy → C6H12O6 + 6O2");
        questionRepository.save(sciQ3);

        // Create Sample Questions for Programming
        Question progQ1 = new Question();
        progQ1.setContent("What does JVM stand for?");
        progQ1.setDifficulty(Difficulty.EASY);
        progQ1.setSubject(programming);
        progQ1.setCorrectAnswer("Java Virtual Machine");
        questionRepository.save(progQ1);

        Question progQ2 = new Question();
        progQ2.setContent("What is the time complexity of binary search?");
        progQ2.setDifficulty(Difficulty.MEDIUM);
        progQ2.setSubject(programming);
        progQ2.setCorrectAnswer("O(log n)");
        questionRepository.save(progQ2);

        Question progQ3 = new Question();
        progQ3.setContent("Implement a function to detect cycle in a linked list");
        progQ3.setDifficulty(Difficulty.HARD);
        progQ3.setSubject(programming);
        progQ3.setCorrectAnswer("Floyd's Cycle Detection Algorithm");
        questionRepository.save(progQ3);

        System.out.println("Sample data initialized successfully!");
    }
}
