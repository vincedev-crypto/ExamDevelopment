# Implemented Algorithms - Adaptive Exam System

## ✅ Currently Implemented Algorithms

### 1. **Adaptive Difficulty Distribution Algorithm**
**Location**: `ExamGenerationService.java` - `generateExam()` method

**Description**: Adjusts the difficulty mix of exam questions based on student mastery level.

**Implementation**:
```java
if (masteryScore < 0.3) {
    // Beginner: 70% Easy, 20% Medium, 10% Hard
    easyCount = (int) (totalQuestions * 0.7);
    mediumCount = (int) (totalQuestions * 0.2);
    hardCount = totalQuestions - easyCount - mediumCount;
} else if (masteryScore < 0.7) {
    // Intermediate: 30% Easy, 50% Medium, 20% Hard
    easyCount = (int) (totalQuestions * 0.3);
    mediumCount = (int) (totalQuestions * 0.5);
    hardCount = totalQuestions - easyCount - mediumCount;
} else {
    // Advanced: 10% Easy, 30% Medium, 60% Hard
    easyCount = (int) (totalQuestions * 0.1);
    mediumCount = (int) (totalQuestions * 0.3);
    hardCount = totalQuestions - easyCount - mediumCount;
}
```

**Key Features**:
- ✅ Cold Start Problem handled (defaults to 0.0 for new students)
- ✅ Progressive difficulty adjustment
- ✅ Zone of Proximal Development implementation

---

### 2. **Mastery Update Algorithm (Weighted Moving Average)**
**Location**: `ExamGenerationService.java` - `updateMastery()` method

**Description**: Updates student mastery based on exam performance using exponential moving average.

**Formula**:
$$\text{New Mastery} = (\text{Current Mastery} \times 0.7) + (\text{Exam Score} \times 0.3)$$

**Implementation**:
```java
double currentMastery = mastery.getMasteryLevel();
double newMastery = (currentMastery * 0.7) + (examScore * 0.3);
mastery.setMasteryLevel(newMastery);
```

**Key Features**:
- ✅ Prevents single exam from drastically changing mastery
- ✅ Gradual adaptation to student performance
- ✅ Per-subject tracking (Lance can be advanced in Math, beginner in Science)

---

### 3. **Discrimination Index (DI) Algorithm**
**Location**: `ExamGenerationService.java` - `updateQuestionStats()` method

**Description**: Measures how well a question differentiates between high and low performers.

**Formula**:
$$DI = P(\text{High Mastery}) - P(\text{Low Mastery})$$

Where:
- $P(\text{High})$ = Proportion of high-mastery students (≥0.7) who answered correctly
- $P(\text{Low})$ = Proportion of low-mastery students (≤0.3) who answered correctly

**Implementation**:
```java
// Track stats for high mastery students
if (studentMastery >= 0.7) {
    question.setUsageByHighMastery(question.getUsageByHighMastery() + 1);
    if (isCorrect) {
        question.setCorrectByHighMastery(question.getCorrectByHighMastery() + 1);
    }
}

// Track stats for low mastery students
else if (studentMastery <= 0.3) {
    question.setUsageByLowMastery(question.getUsageByLowMastery() + 1);
    if (isCorrect) {
        question.setCorrectByLowMastery(question.getCorrectByLowMastery() + 1);
    }
}

// Calculate DI
double pHigh = (double) correctByHighMastery / usageByHighMastery;
double pLow = (double) correctByLowMastery / usageByLowMastery;
discriminationIndex = pHigh - pLow;
```

**Interpretation**:
- **DI > 0.4**: Excellent question (good discrimination)
- **DI 0.2 - 0.4**: Good question
- **DI < 0.2**: Poor question (doesn't discriminate well)
- **DI < 0**: Very poor (low performers do better than high performers!)

**Key Features**:
- ✅ Automatic quality assessment
- ✅ Identifies problematic questions
- ✅ Data-driven question evaluation

---

### 4. **Dynamic Difficulty Reclassification Algorithm**
**Location**: `ExamGenerationService.java` - `updateQuestionStats()` method

**Description**: Automatically adjusts question difficulty based on actual student performance.

**Implementation**:
```java
if (question.getUsageCount() >= 10) {
    double successRate = (double) correctCount / usageCount;

    // Too hard: < 30% success rate
    if (successRate < 0.3 && question.getDifficulty() != Difficulty.HARD) {
        question.setDifficulty(Difficulty.HARD);
    } 
    // Too easy: > 80% success rate
    else if (successRate > 0.8 && question.getDifficulty() != Difficulty.EASY) {
        question.setDifficulty(Difficulty.EASY);
    }
    // Just right: 30-80% success rate
    else if (successRate >= 0.3 && successRate <= 0.8 
             && question.getDifficulty() != Difficulty.MEDIUM) {
        question.setDifficulty(Difficulty.MEDIUM);
    }
}
```

**Thresholds**:
- **EASY**: Success rate > 80%
- **MEDIUM**: Success rate 30% - 80%
- **HARD**: Success rate < 30%

**Key Features**:
- ✅ Corrects teacher misclassification
- ✅ Requires minimum 10 attempts before reclassifying
- ✅ Self-improving system

---

### 5. **Question Statistics Tracking Algorithm**
**Location**: `Question.java` model + `ExamGenerationService.java`

**Description**: Tracks comprehensive usage statistics for each question.

**Tracked Metrics**:
```java
private int usageCount = 0;              // Total times used
private int correctCount = 0;            // Total times answered correctly
private int usageByHighMastery = 0;      // Used by advanced students
private int correctByHighMastery = 0;    // Correct by advanced students
private int usageByLowMastery = 0;       // Used by beginners
private int correctByLowMastery = 0;     // Correct by beginners
private Double discriminationIndex = 0.0; // DI score
```

**Key Features**:
- ✅ Comprehensive performance tracking
- ✅ Enables data-driven decisions
- ✅ Supports quality assurance

---

### 6. **Randomized Question Selection Algorithm**
**Location**: `QuestionRepository.java` - `findRandomQuestions()` query

**Description**: Prevents memorization by randomly selecting questions within each difficulty level.

**Implementation**:
```java
@Query(value = "SELECT * FROM question q 
                WHERE q.subject_id = :subjectId 
                AND q.difficulty = :difficulty 
                ORDER BY RAND() 
                LIMIT :limit", 
       nativeQuery = true)
List<Question> findRandomQuestions(@Param("subjectId") Long subjectId, 
                                   @Param("difficulty") String difficulty, 
                                   @Param("limit") int limit);
```

**Key Features**:
- ✅ Different questions each exam
- ✅ Fair assessment
- ✅ Prevents cheating

---

### 7. **Complete Feedback Loop Algorithm**
**Location**: `ExamGenerationService.java` - `processExamResult()` method

**Description**: Comprehensive result processing that updates both student mastery and question statistics.

**Workflow**:
```java
1. Calculate exam score from correct answers
2. Save score to Exam entity
3. Update student mastery level
4. For each question in exam:
   - Update usage count
   - Update correct/incorrect count
   - Update high/low mastery stats
   - Recalculate discrimination index
   - Potentially reclassify difficulty
```

**Key Features**:
- ✅ Holistic system update
- ✅ Bidirectional learning (student & question)
- ✅ Continuous improvement

---

## 📊 Algorithm Integration Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. EXAM GENERATION REQUEST                                  │
│    - Student ID, Subject ID, Total Questions                │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. FETCH STUDENT MASTERY (Algorithm #2)                     │
│    - Query StudentSubjectMastery table                       │
│    - Default to 0.0 if no history (Cold Start)              │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. APPLY ADAPTIVE DISTRIBUTION (Algorithm #1)               │
│    - If mastery < 0.3: 70% Easy, 20% Med, 10% Hard         │
│    - If mastery 0.3-0.7: 30% Easy, 50% Med, 20% Hard       │
│    - If mastery > 0.7: 10% Easy, 30% Med, 60% Hard         │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. RANDOM QUESTION SELECTION (Algorithm #6)                 │
│    - Fetch random questions for each difficulty             │
│    - Prevent memorization                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. GENERATE & DISPLAY EXAM                                  │
│    - Create Exam entity                                     │
│    - Render questions with checkboxes                       │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. STUDENT COMPLETES EXAM                                   │
│    - Teacher marks correct answers                          │
│    - Submit results                                         │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. PROCESS RESULTS (Algorithm #7)                           │
│    - Calculate score                                        │
│    - Update mastery (Algorithm #2)                          │
│    - Update question stats (Algorithm #5)                   │
│    - Calculate DI (Algorithm #3)                            │
│    - Reclassify difficulty (Algorithm #4)                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 What Each Algorithm Achieves

### For Students:
1. **Personalized Difficulty**: Each exam matches their current level
2. **Progressive Learning**: Gradual increase in difficulty as they improve
3. **Fair Assessment**: Questions are appropriate for their ability
4. **Motivation**: Not too easy (boring) or too hard (frustrating)

### For Teachers:
1. **Automatic Question Quality Check**: DI identifies bad questions
2. **Difficulty Validation**: System corrects misclassified questions
3. **Performance Analytics**: Detailed stats on each question
4. **Reduced Manual Work**: System adapts automatically

### For the System:
1. **Self-Improvement**: Gets better with more data
2. **Data-Driven**: Decisions based on actual performance
3. **Scalable**: Works for any number of students/subjects
4. **Robust**: Handles edge cases (new students, new questions)

---

## 🔬 Academic Foundations

### 1. **Item Response Theory (IRT)**
- Discrimination Index is a simplified IRT metric
- Difficulty reclassification based on empirical data

### 2. **Adaptive Testing**
- CAT (Computerized Adaptive Testing) principles
- Dynamic difficulty adjustment

### 3. **Psychometric Validity**
- Discrimination Index ensures question quality
- Success rate thresholds based on assessment theory

### 4. **Learning Theory**
- Vygotsky's Zone of Proximal Development
- Formative Assessment principles
- Bloom's Taxonomy alignment

---

## 📈 Future Enhancement Opportunities

### 1. **Item Response Theory (Full IRT Model)**
```
P(correct) = 1 / (1 + e^(-a(θ - b)))
Where: θ = ability, b = difficulty, a = discrimination
```

### 2. **Bayesian Knowledge Tracing**
- Probabilistic student knowledge modeling
- More accurate mastery estimation

### 3. **Multi-dimensional IRT**
- Track multiple skill dimensions
- Cross-subject correlation analysis

### 4. **Automated Question Generation**
- AI-generated questions
- Difficulty prediction before use

### 5. **Time-based Metrics**
- Response time analysis
- Speed-accuracy tradeoff

---

## ✅ Summary

**All Core Algorithms Implemented**:
- ✅ Adaptive Difficulty Distribution
- ✅ Mastery Update (Weighted Moving Average)
- ✅ Discrimination Index Calculation
- ✅ Dynamic Difficulty Reclassification
- ✅ Question Statistics Tracking
- ✅ Randomized Selection
- ✅ Complete Feedback Loop

**System is Production-Ready For**:
- Personalized exam generation
- Automatic mastery tracking
- Self-improving question bank
- Data-driven quality assurance

**The thesis algorithms are fully implemented and operational!** 🎓
