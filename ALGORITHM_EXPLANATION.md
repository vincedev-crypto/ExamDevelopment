# Algorithm & Difficulty Logic - Thesis Documentation

## 📋 Overview
This document provides detailed answers to the key questions about how the adaptive exam system handles difficulty and distribution.

---

## ❓ Question 1: How does the system set difficulty when teachers upload questions?

### Answer:
The system **does NOT automatically determine difficulty**. Instead:

1. **Manual Tagging**: When a teacher uploads a question via `/question/upload`, they must select:
   - EASY
   - MEDIUM
   - HARD

2. **Database Storage**: The difficulty is stored as an `enum` in the `Question` table:
   ```java
   public enum Difficulty {
       EASY, MEDIUM, HARD
   }
   ```

3. **No Automatic Arrangement**: Questions are NOT automatically sorted ascending/descending. They stay in the database tagged with their difficulty.

### Why This Approach?
- **Subject Matter Expertise**: Only the teacher knows if "What is 2+2?" is truly easy or if "Solve differential equations" is hard
- **Context Matters**: A question about basic Python syntax is EASY for CS students but HARD for Math majors
- **Future Enhancement**: Could use Machine Learning to suggest difficulty based on historical performance data

---

## ❓ Question 2: What if there is NO history in the system?

### The Cold Start Problem

**Scenario**: Lance is a **brand new student** who has never taken an exam in the system.

### Solution: Default Profiling

```java
// From ExamGenerationService.java
Optional<StudentSubjectMastery> masteryOpt = 
    masteryRepository.findByStudentAndSubject(student, subject);

// Default to 0.0 if no history exists
double masteryScore = masteryOpt.map(StudentSubjectMastery::getMasteryLevel)
                                .orElse(0.0);
```

### Initial Distribution (Beginner Level)
When `masteryScore = 0.0` (new student):

| Difficulty | Percentage | Reason |
|------------|-----------|--------|
| EASY | 70% | Build confidence |
| MEDIUM | 20% | Assess potential |
| HARD | 10% | Identify top performers |

**Example**: If generating 20 questions:
- 14 Easy questions
- 4 Medium questions
- 2 Hard questions

### After First Exam
1. Student completes the exam
2. Teacher scores it (0.0 to 1.0 scale)
3. System updates mastery:
   ```java
   newMastery = (oldMastery × 0.7) + (examScore × 0.3)
   ```
4. For Lance's first exam: `newMastery = (0.0 × 0.7) + (0.85 × 0.3) = 0.255`

---

## ❓ Question 3: Lance vs Henry - Different Subject Strengths

### Scenario:
- **Lance**: Great at Math, Weak at Science
- **Henry**: Weak at Math, Good at Programming

### How the System Handles This

#### Database Design: Per-Student, Per-Subject Tracking

```java
@Entity
public class StudentSubjectMastery {
    @ManyToOne
    private Student student;
    
    @ManyToOne
    private Subject subject;
    
    private Double masteryLevel; // 0.0 to 1.0
}
```

**Database Example**:

| Student | Subject | Mastery Level |
|---------|---------|---------------|
| Lance | Math | 0.85 (Advanced) |
| Lance | Science | 0.25 (Beginner) |
| Henry | Math | 0.30 (Intermediate) |
| Henry | Programming | 0.80 (Advanced) |

#### Algorithm in Action

**When Lance generates a Math exam** (mastery = 0.85 > 0.7):
```
Distribution: 10% Easy, 30% Medium, 60% Hard
For 20 questions:
  - 2 Easy
  - 6 Medium
  - 12 Hard ← Challenging!
```

**When Lance generates a Science exam** (mastery = 0.25 < 0.3):
```
Distribution: 70% Easy, 20% Medium, 10% Hard
For 20 questions:
  - 14 Easy ← Confidence building
  - 4 Medium
  - 2 Hard
```

**When Henry generates a Math exam** (mastery = 0.30, borderline):
```
Distribution: 30% Easy, 50% Medium, 20% Hard
For 20 questions:
  - 6 Easy
  - 10 Medium ← Balanced
  - 4 Hard
```

**When Henry generates a Programming exam** (mastery = 0.80 > 0.7):
```
Distribution: 10% Easy, 30% Medium, 60% Hard
For 20 questions:
  - 2 Easy
  - 6 Medium
  - 12 Hard ← Advanced level
```

### Code Implementation

```java
// ExamGenerationService.java - Line 28-49
public Exam generateExam(Student student, Subject subject, int totalQuestions) {
    // 1. Get mastery for THIS SPECIFIC student-subject combination
    Optional<StudentSubjectMastery> masteryOpt = 
        masteryRepository.findByStudentAndSubject(student, subject);
    
    double masteryScore = masteryOpt.map(StudentSubjectMastery::getMasteryLevel)
                                    .orElse(0.0);

    // 2. Determine distribution based on mastery
    int easyCount, mediumCount, hardCount;

    if (masteryScore < 0.3) {
        // Beginner (Lance in Science, Henry in Math)
        easyCount = (int) (totalQuestions * 0.7);
        mediumCount = (int) (totalQuestions * 0.2);
        hardCount = totalQuestions - easyCount - mediumCount;
    } else if (masteryScore < 0.7) {
        // Intermediate (New students, borderline cases)
        easyCount = (int) (totalQuestions * 0.3);
        mediumCount = (int) (totalQuestions * 0.5);
        hardCount = totalQuestions - easyCount - mediumCount;
    } else {
        // Advanced (Lance in Math, Henry in Programming)
        easyCount = (int) (totalQuestions * 0.1);
        mediumCount = (int) (totalQuestions * 0.3);
        hardCount = totalQuestions - easyCount - mediumCount;
    }
    
    // ... fetch and return questions
}
```

---

## ❓ Question 4: Distribution & Quality of Exams

### Question Distribution Strategy

#### 1. Quantity Control
The system uses a **Blueprint Approach** (Table of Specifications):

```java
public Exam generateExam(Student student, Subject subject, int totalQuestions)
```

**Example Inputs**:
- Total Questions: 50
- Subject: Mathematics
- Student: Lance (High mastery)

**System Calculates**:
```
50 questions × 10% Easy = 5 Easy questions
50 questions × 30% Medium = 15 Medium questions
50 questions × 60% Hard = 30 Hard questions
```

#### 2. Random Selection Within Difficulty
```java
// QuestionRepository.java
@Query(value = "SELECT * FROM question q 
                WHERE q.subject_id = :subjectId 
                AND q.difficulty = :difficulty 
                ORDER BY RAND() 
                LIMIT :limit", 
       nativeQuery = true)
List<Question> findRandomQuestions(
    @Param("subjectId") Long subjectId, 
    @Param("difficulty") String difficulty, 
    @Param("limit") int limit
);
```

**Benefits**:
- Prevents memorization (different questions each time)
- Ensures coverage across all difficulty levels
- Maintains consistent challenge level

### Quality Assurance Mechanisms

#### Current Implementation (Basic)
1. **Teacher Validation**: Teachers review and tag questions appropriately
2. **Fixed Distribution**: Ensures balanced assessment
3. **Subject Alignment**: Questions match the exam subject

#### Future Enhancements (Advanced)

##### 1. Discrimination Index (DI)
Measures how well a question differentiates between high/low performers:

```
DI = (% of high-scorers who got it right) - (% of low-scorers who got it right)

If DI < 0.2: Poor question (doesn't discriminate)
If DI > 0.4: Excellent question
```

**Example**:
- Question marked "EASY"
- 90% of top students get it right
- 95% of bottom students get it right
- DI = 0.90 - 0.95 = -0.05 ❌ **Flag for review**

##### 2. Dynamic Difficulty Reclassification
```java
// Pseudo-code for future enhancement
if (question.getDifficulty() == EASY && 
    question.getFailRate() > 0.8) {
    question.setDifficulty(HARD);
    questionRepository.save(question);
}
```

##### 3. Item Response Theory (IRT)
Mathematical model that:
- Estimates question difficulty objectively
- Accounts for student ability
- Predicts probability of correct answer

**Formula** (simplified):
```
P(correct) = 1 / (1 + e^(-a(θ - b)))

Where:
  θ = student ability
  b = question difficulty
  a = discrimination parameter
```

### Quality Distribution Formula

**Current System**:
```
Quality Score = Teacher Rating (1-3)
```

**Enhanced System** (Future):
```
Quality Score = (Discrimination Index × 0.4) + 
                (Difficulty Accuracy × 0.3) + 
                (Coverage Index × 0.3)
```

---

## 🔬 Algorithm Workflow Summary

```
┌─────────────────────────────────────────────────┐
│ TEACHER UPLOADS QUESTION                        │
│ - Content: "What is 2+2?"                       │
│ - Subject: Math                                 │
│ - Difficulty: EASY (manual tag)                 │
│ - Stored in database                            │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ STUDENT REQUESTS EXAM                           │
│ - Student: Lance                                │
│ - Subject: Math                                 │
│ - Total Questions: 20                           │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ SYSTEM CHECKS MASTERY                           │
│ - Query: StudentSubjectMastery table            │
│ - Lance + Math = 0.85 (Advanced)                │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ APPLY DISTRIBUTION ALGORITHM                    │
│ - If mastery >= 0.7: Advanced distribution      │
│ - 10% Easy, 30% Medium, 60% Hard                │
│ - Calculate: 2 Easy, 6 Medium, 12 Hard          │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ FETCH QUESTIONS                                 │
│ - Randomly select 2 EASY Math questions         │
│ - Randomly select 6 MEDIUM Math questions       │
│ - Randomly select 12 HARD Math questions        │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ GENERATE & DISPLAY EXAM                         │
│ - Create Exam entity                            │
│ - Link to Lance and Math subject                │
│ - Save to database                              │
│ - Render on screen                              │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ STUDENT COMPLETES EXAM                          │
│ - Score submitted: 0.90 (90%)                   │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ UPDATE MASTERY                                  │
│ - Old Mastery: 0.85                             │
│ - New Mastery = (0.85 × 0.7) + (0.90 × 0.3)     │
│ - New Mastery = 0.595 + 0.27 = 0.865            │
│ - Save updated mastery                          │
└─────────────────────────────────────────────────┘
```

---

## 📊 Key Takeaways

1. **Teacher Control**: Difficulty is set manually by teachers, not the algorithm
2. **Cold Start Solution**: New students default to beginner level (70% Easy)
3. **Personalized Learning**: Each student-subject combination has independent mastery tracking
4. **Quality Through Distribution**: Balanced difficulty ensures comprehensive assessment
5. **Continuous Adaptation**: Mastery scores evolve with each exam

---

## 🎓 Academic Justification

### Why This Approach Works:

1. **Vygotsky's Zone of Proximal Development**: 
   - Questions slightly above current level promote growth
   - Too easy = boredom, Too hard = frustration

2. **Bloom's Taxonomy Alignment**:
   - EASY = Remember/Understand
   - MEDIUM = Apply/Analyze
   - HARD = Evaluate/Create

3. **Formative Assessment Theory**:
   - Continuous feedback (mastery updates)
   - Adaptive instruction (difficulty adjustment)

4. **Psychometric Validity**:
   - Consistent measurement (fixed distribution)
   - Reliable scoring (objective tagging)

---

**For more details, see the main README.md and source code comments.**
