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
    private QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty (prevents duplicates on restart)
        if (subjectRepository.count() > 0) {
            System.out.println("Database already initialized. Skipping data initialization.");
            return;
        }
        
        System.out.println("Initializing database with sample data...");
        
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

        // Students will register through the registration system
        // No test data for students - they must verify their email

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

        // Add more questions for each subject to support 30-question exams
        addMoreMathQuestions(math);
        addMoreScienceQuestions(science);
        addMoreProgrammingQuestions(programming);

        System.out.println("Sample data initialized successfully with " + questionRepository.count() + " questions!");
    }
    
    private void addMoreMathQuestions(Subject math) {
        // Additional EASY Math Questions
        createQuestion("What is 5 x 6?", Difficulty.EASY, math, "30");
        createQuestion("What is 100 - 47?", Difficulty.EASY, math, "53");
        createQuestion("What is the square root of 64?", Difficulty.EASY, math, "8");
        createQuestion("What is 15% of 200?", Difficulty.EASY, math, "30");
        createQuestion("What is the area of a rectangle with length 5 and width 3?", Difficulty.EASY, math, "15");
        createQuestion("What is 3/4 as a decimal?", Difficulty.EASY, math, "0.75");
        createQuestion("What is 2^5?", Difficulty.EASY, math, "32");
        createQuestion("What is the perimeter of a square with side 7?", Difficulty.EASY, math, "28");
        createQuestion("What is 12 + 8?", Difficulty.EASY, math, "20");
        createQuestion("What is 9 x 9?", Difficulty.EASY, math, "81");
        
        // Additional MEDIUM Math Questions
        createQuestion("Solve: 3x - 7 = 11", Difficulty.MEDIUM, math, "x = 6");
        createQuestion("What is the slope of a line through points (2,3) and (4,7)?", Difficulty.MEDIUM, math, "2");
        createQuestion("Factor: x^2 + 5x + 6", Difficulty.MEDIUM, math, "(x + 2)(x + 3)");
        createQuestion("What is sin(30°)?", Difficulty.MEDIUM, math, "0.5");
        createQuestion("Find the quadratic formula", Difficulty.MEDIUM, math, "x = (-b ± √(b²-4ac)) / 2a");
        createQuestion("What is the area of a circle with radius 5?", Difficulty.MEDIUM, math, "25π or 78.54");
        createQuestion("Solve: 2(x + 3) = 4x - 2", Difficulty.MEDIUM, math, "x = 4");
        createQuestion("What is log₁₀(1000)?", Difficulty.MEDIUM, math, "3");
        createQuestion("Find the sum of arithmetic series: 2 + 4 + 6 + ... + 20", Difficulty.MEDIUM, math, "110");
        createQuestion("What is the distance between points (1,2) and (4,6)?", Difficulty.MEDIUM, math, "5");
        
        // Additional HARD Math Questions
        createQuestion("Find the integral of 3x^2 dx", Difficulty.HARD, math, "x^3 + C");
        createQuestion("Solve: sin²x + cos²x = ?", Difficulty.HARD, math, "1");
        createQuestion("Find the limit as x approaches 0 of sin(x)/x", Difficulty.HARD, math, "1");
        createQuestion("Find eigenvalues of matrix [[2,1],[1,2]]", Difficulty.HARD, math, "3 and 1");
        createQuestion("Solve differential equation: dy/dx = 2y", Difficulty.HARD, math, "y = Ce^(2x)");
        createQuestion("Find Taylor series of e^x around x=0", Difficulty.HARD, math, "1 + x + x²/2! + x³/3! + ...");
        createQuestion("Prove that √2 is irrational", Difficulty.HARD, math, "Proof by contradiction");
        createQuestion("Find the inverse of matrix [[1,2],[3,4]]", Difficulty.HARD, math, "[[-2,1],[1.5,-0.5]]");
        createQuestion("Solve: ∫(1/(1+x²))dx", Difficulty.HARD, math, "arctan(x) + C");
        createQuestion("Find partial derivative ∂/∂x(x²y + y³)", Difficulty.HARD, math, "2xy");
    }
    
    private void addMoreScienceQuestions(Subject science) {
        // Additional EASY Science Questions
        createQuestion("What gas do plants absorb from the atmosphere?", Difficulty.EASY, science, "Carbon dioxide (CO₂)");
        createQuestion("What is the boiling point of water in Celsius?", Difficulty.EASY, science, "100°C");
        createQuestion("What is the largest organ in the human body?", Difficulty.EASY, science, "Skin");
        createQuestion("What is the speed of light?", Difficulty.EASY, science, "300,000 km/s");
        createQuestion("What is the chemical symbol for gold?", Difficulty.EASY, science, "Au");
        createQuestion("How many bones are in the adult human body?", Difficulty.EASY, science, "206");
        createQuestion("What planet is known as the Red Planet?", Difficulty.EASY, science, "Mars");
        createQuestion("What is the powerhouse of the cell?", Difficulty.EASY, science, "Mitochondria");
        createQuestion("What is the pH of pure water?", Difficulty.EASY, science, "7");
        createQuestion("What gas do humans exhale?", Difficulty.EASY, science, "Carbon dioxide (CO₂)");
        
        // Additional MEDIUM Science Questions
        createQuestion("What is the molecular formula of glucose?", Difficulty.MEDIUM, science, "C₆H₁₂O₆");
        createQuestion("What is Ohm's Law?", Difficulty.MEDIUM, science, "V = IR");
        createQuestion("What is the difference between mitosis and meiosis?", Difficulty.MEDIUM, science, "Mitosis produces 2 identical cells, meiosis produces 4 different cells");
        createQuestion("What is the ideal gas law?", Difficulty.MEDIUM, science, "PV = nRT");
        createQuestion("What is the difference between DNA and RNA?", Difficulty.MEDIUM, science, "DNA is double-stranded with thymine, RNA is single-stranded with uracil");
        createQuestion("What is kinetic energy formula?", Difficulty.MEDIUM, science, "KE = ½mv²");
        createQuestion("What is the electron configuration of oxygen?", Difficulty.MEDIUM, science, "1s² 2s² 2p⁴");
        createQuestion("What is the law of conservation of energy?", Difficulty.MEDIUM, science, "Energy cannot be created or destroyed");
        createQuestion("What is osmosis?", Difficulty.MEDIUM, science, "Movement of water across a semipermeable membrane");
        createQuestion("What is the difference between exothermic and endothermic?", Difficulty.MEDIUM, science, "Exothermic releases heat, endothermic absorbs heat");
        
        // Additional HARD Science Questions
        createQuestion("Explain the Krebs cycle in cellular respiration", Difficulty.HARD, science, "Series of chemical reactions to generate energy through oxidation of acetyl-CoA");
        createQuestion("What is Heisenberg's Uncertainty Principle?", Difficulty.HARD, science, "Cannot simultaneously know exact position and momentum of a particle");
        createQuestion("Describe the structure of benzene and its resonance", Difficulty.HARD, science, "C₆H₆ with delocalized electrons in resonance structures");
        createQuestion("Explain the theory of evolution by natural selection", Difficulty.HARD, science, "Organisms with advantageous traits survive and reproduce more successfully");
        createQuestion("What is the Schrödinger equation?", Difficulty.HARD, science, "Fundamental equation of quantum mechanics describing wave function");
        createQuestion("Explain DNA replication process", Difficulty.HARD, science, "Semi-conservative replication using DNA polymerase");
        createQuestion("What is entropy in thermodynamics?", Difficulty.HARD, science, "Measure of disorder or randomness in a system");
        createQuestion("Describe the electron transport chain", Difficulty.HARD, science, "Series of protein complexes transferring electrons to generate ATP");
        createQuestion("What is Le Chatelier's Principle?", Difficulty.HARD, science, "System at equilibrium adjusts to counteract imposed changes");
        createQuestion("Explain quantum entanglement", Difficulty.HARD, science, "Quantum states of particles remain correlated regardless of distance");
    }
    
    private void addMoreProgrammingQuestions(Subject programming) {
        // Additional EASY Programming Questions
        createQuestion("What does HTML stand for?", Difficulty.EASY, programming, "HyperText Markup Language");
        createQuestion("What is a variable?", Difficulty.EASY, programming, "A container for storing data values");
        createQuestion("What does CPU stand for?", Difficulty.EASY, programming, "Central Processing Unit");
        createQuestion("What is an array?", Difficulty.EASY, programming, "A collection of elements of the same type");
        createQuestion("What does SQL stand for?", Difficulty.EASY, programming, "Structured Query Language");
        createQuestion("What is a loop?", Difficulty.EASY, programming, "A structure that repeats a block of code");
        createQuestion("What is a function?", Difficulty.EASY, programming, "A reusable block of code that performs a specific task");
        createQuestion("What does RAM stand for?", Difficulty.EASY, programming, "Random Access Memory");
        createQuestion("What is a boolean?", Difficulty.EASY, programming, "A data type with two values: true or false");
        createQuestion("What is debugging?", Difficulty.EASY, programming, "Finding and fixing errors in code");
        
        // Additional MEDIUM Programming Questions
        createQuestion("What is the difference between == and === in JavaScript?", Difficulty.MEDIUM, programming, "== checks value, === checks value and type");
        createQuestion("What is recursion?", Difficulty.MEDIUM, programming, "A function calling itself");
        createQuestion("What is the time complexity of quicksort?", Difficulty.MEDIUM, programming, "O(n log n) average case");
        createQuestion("What is polymorphism in OOP?", Difficulty.MEDIUM, programming, "Ability of objects to take multiple forms");
        createQuestion("What is a stack data structure?", Difficulty.MEDIUM, programming, "LIFO (Last In First Out) data structure");
        createQuestion("What is the difference between ArrayList and LinkedList?", Difficulty.MEDIUM, programming, "ArrayList uses array, LinkedList uses nodes with pointers");
        createQuestion("What is a hash table?", Difficulty.MEDIUM, programming, "Data structure using key-value pairs with hash function");
        createQuestion("What is encapsulation?", Difficulty.MEDIUM, programming, "Bundling data and methods that work on data");
        createQuestion("What is the purpose of try-catch block?", Difficulty.MEDIUM, programming, "Exception handling");
        createQuestion("What is Big O notation?", Difficulty.MEDIUM, programming, "Describes algorithm time/space complexity");
        
        // Additional HARD Programming Questions
        createQuestion("Implement merge sort algorithm", Difficulty.HARD, programming, "Divide and conquer sorting with O(n log n)");
        createQuestion("Explain how garbage collection works in Java", Difficulty.HARD, programming, "Automatic memory management removing unused objects");
        createQuestion("What is dynamic programming?", Difficulty.HARD, programming, "Optimization technique storing results of subproblems");
        createQuestion("Implement a binary search tree", Difficulty.HARD, programming, "Tree structure with left < parent < right");
        createQuestion("What is the difference between process and thread?", Difficulty.HARD, programming, "Process is independent, thread shares memory");
        createQuestion("Explain the concept of closures", Difficulty.HARD, programming, "Function accessing variables from outer scope");
        createQuestion("What is the CAP theorem?", Difficulty.HARD, programming, "Consistency, Availability, Partition tolerance - pick 2");
        createQuestion("Implement Dijkstra's shortest path algorithm", Difficulty.HARD, programming, "Greedy algorithm finding shortest paths from source");
        createQuestion("What are design patterns? Name 3.", Difficulty.HARD, programming, "Reusable solutions: Singleton, Factory, Observer");
        createQuestion("Explain ACID properties in databases", Difficulty.HARD, programming, "Atomicity, Consistency, Isolation, Durability");
    }
    
    private void createQuestion(String content, Difficulty difficulty, Subject subject, String answer) {
        Question q = new Question();
        q.setContent(content);
        q.setDifficulty(difficulty);
        q.setSubject(subject);
        q.setCorrectAnswer(answer);
        questionRepository.save(q);
    }
}
