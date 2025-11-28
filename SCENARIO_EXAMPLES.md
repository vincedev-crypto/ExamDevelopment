# System Behavior Examples

## 📊 Comprehensive Scenario Analysis

### Scenario Comparison Table

| Scenario | Student | Subject | Mastery Score | Easy % | Medium % | Hard % | Example Distribution (20 Q) |
|----------|---------|---------|--------------|--------|----------|--------|----------------------------|
| **Cold Start** | Lance (New) | Math | 0.0 (Default) | 70% | 20% | 10% | 14 Easy, 4 Med, 2 Hard |
| **Cold Start** | Henry (New) | Science | 0.0 (Default) | 70% | 20% | 10% | 14 Easy, 4 Med, 2 Hard |
| **After 1st Exam** | Lance | Math | 0.25 (Low) | 70% | 20% | 10% | 14 Easy, 4 Med, 2 Hard |
| **Intermediate** | Lance | Math | 0.50 (Mid) | 30% | 50% | 20% | 6 Easy, 10 Med, 4 Hard |
| **Advanced** | Lance | Math | 0.85 (High) | 10% | 30% | 60% | 2 Easy, 6 Med, 12 Hard |
| **Subject Switch** | Lance | Science | 0.25 (Low) | 70% | 20% | 10% | 14 Easy, 4 Med, 2 Hard |
| **Different Student** | Henry | Math | 0.30 (Border) | 30% | 50% | 20% | 6 Easy, 10 Med, 4 Hard |
| **Henry Advanced** | Henry | Programming | 0.80 (High) | 10% | 30% | 60% | 2 Easy, 6 Med, 12 Hard |

---

## 🎯 Detailed Scenario Walkthrough

### Scenario 1: Brand New Student (Cold Start)

**Context**: Lance has just registered. No exam history.

```
┌─────────────────────────────────────┐
│ Student: Lance                      │
│ Subject: Mathematics                │
│ History: NONE                       │
│ Mastery: 0.0 (Default)              │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│ ALGORITHM DECISION:                 │
│ mastery (0.0) < 0.3 → BEGINNER      │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│ EXAM COMPOSITION (20 questions):    │
│ ✓ 14 EASY questions (70%)           │
│ ✓ 4 MEDIUM questions (20%)          │
│ ✓ 2 HARD questions (10%)            │
│                                     │
│ Purpose: Establish baseline         │
└─────────────────────────────────────┘
```

**Result**: A confidence-building exam that identifies baseline ability.

---

### Scenario 2: Same Student, Different Subject

**Context**: Lance (now experienced in Math) tries Science for first time.

```
┌────────────────────────────────────────┐
│ Student: Lance                         │
│ Previous Exams:                        │
│   - Math: 5 exams, mastery = 0.85      │
│   - Science: NONE                      │
└────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────┐
│ DATABASE QUERY:                        │
│ SELECT * FROM StudentSubjectMastery    │
│ WHERE student_id = Lance.id            │
│   AND subject_id = Science.id          │
│                                        │
│ Result: EMPTY (No record found)        │
└────────────────────────────────────────┘
            ↓
┌────────────────────────────────────────┐
│ SCIENCE EXAM:                          │
│ ✓ 14 EASY (70%)  ← Treated as beginner│
│ ✓ 4 MEDIUM (20%)                       │
│ ✓ 2 HARD (10%)                         │
│                                        │
│ MATH EXAM (if taken simultaneously):   │
│ ✓ 2 EASY (10%)   ← Advanced level     │
│ ✓ 6 MEDIUM (30%)                       │
│ ✓ 12 HARD (60%)                        │
└────────────────────────────────────────┘
```

**Key Insight**: Mastery is **NOT transferable** across subjects!

---

### Scenario 3: Two Students, Same Subject, Different Levels

**Context**: Lance and Henry both take Math exam on the same day.

| Metric | Lance (Math) | Henry (Math) |
|--------|-------------|-------------|
| Previous Math Exams | 5 exams | 2 exams |
| Average Math Score | 88% | 62% |
| Current Mastery | 0.85 | 0.30 |
| Difficulty Tier | Advanced | Intermediate |

**Lance's Math Exam** (Mastery 0.85):
```
Total: 20 questions
├── 2 Easy (10%)    - "What is 5 + 3?"
├── 6 Medium (30%)  - "Solve: 3x + 7 = 22"
└── 12 Hard (60%)   - "Find derivative of f(x) = ln(x²+1)"
```

**Henry's Math Exam** (Mastery 0.30):
```
Total: 20 questions
├── 6 Easy (30%)    - "What is 5 + 3?"
├── 10 Medium (50%) - "Solve: 3x + 7 = 22"
└── 4 Hard (20%)    - "Find derivative of f(x) = ln(x²+1)"
```

**Observation**: 
- Both might get the same EASY question ("5 + 3")
- Lance gets 3× more HARD questions than Henry
- Henry gets 3× more EASY questions than Lance

---

### Scenario 4: Mastery Evolution Over Time

**Context**: Tracking Lance's Math journey over 6 exams.

| Exam # | Exam Score | Old Mastery | Calculation | New Mastery | Next Exam Distribution |
|--------|-----------|-------------|-------------|-------------|----------------------|
| 1 | 75% (0.75) | 0.0 | (0.0×0.7)+(0.75×0.3) | 0.225 | 70% Easy, 20% Med, 10% Hard |
| 2 | 82% (0.82) | 0.225 | (0.225×0.7)+(0.82×0.3) | 0.404 | 30% Easy, 50% Med, 20% Hard |
| 3 | 78% (0.78) | 0.404 | (0.404×0.7)+(0.78×0.3) | 0.517 | 30% Easy, 50% Med, 20% Hard |
| 4 | 85% (0.85) | 0.517 | (0.517×0.7)+(0.85×0.3) | 0.617 | 30% Easy, 50% Med, 20% Hard |
| 5 | 91% (0.91) | 0.617 | (0.617×0.7)+(0.91×0.3) | 0.705 | 10% Easy, 30% Med, 60% Hard |
| 6 | 88% (0.88) | 0.705 | (0.705×0.7)+(0.88×0.3) | 0.758 | 10% Easy, 30% Med, 60% Hard |

**Mastery Update Formula**:
```java
newMastery = (oldMastery × 0.7) + (examScore × 0.3)
```

**Graph Visualization**:
```
Mastery
1.0 |                                    
0.9 |                                    
0.8 |                              ●─────●
0.7 |                         ●────┘      
0.6 |                   ●─────┘           
0.5 |             ●─────┘                 
0.4 |       ●─────┘                       
0.3 |  ●────┘                             
0.2 | ┘                                   
0.1 |                                     
0.0 └────┬────┬────┬────┬────┬────┬────
       E1   E2   E3   E4   E5   E6   →
```

**Notice**: 
- Gradual progression (weighted average prevents drastic swings)
- Transitions from Beginner → Intermediate → Advanced
- Stabilizes around 0.75-0.80 (realistic mastery ceiling)

---

## 🔄 Question Distribution Logic

### Code Walkthrough

```java
// ExamGenerationService.java

public Exam generateExam(Student student, Subject subject, int totalQuestions) {
    // STEP 1: Fetch mastery (defaults to 0.0 if not found)
    Optional<StudentSubjectMastery> masteryOpt = 
        masteryRepository.findByStudentAndSubject(student, subject);
    double masteryScore = masteryOpt.map(StudentSubjectMastery::getMasteryLevel)
                                    .orElse(0.0);

    // STEP 2: Determine distribution
    int easyCount, mediumCount, hardCount;

    if (masteryScore < 0.3) {
        // BEGINNER TIER
        easyCount = (int) (totalQuestions * 0.7);    // 70%
        mediumCount = (int) (totalQuestions * 0.2);  // 20%
        hardCount = totalQuestions - easyCount - mediumCount; // 10%
    } 
    else if (masteryScore < 0.7) {
        // INTERMEDIATE TIER
        easyCount = (int) (totalQuestions * 0.3);    // 30%
        mediumCount = (int) (totalQuestions * 0.5);  // 50%
        hardCount = totalQuestions - easyCount - mediumCount; // 20%
    } 
    else {
        // ADVANCED TIER
        easyCount = (int) (totalQuestions * 0.1);    // 10%
        mediumCount = (int) (totalQuestions * 0.3);  // 30%
        hardCount = totalQuestions - easyCount - mediumCount; // 60%
    }

    // STEP 3: Fetch random questions
    List<Question> examQuestions = new ArrayList<>();
    examQuestions.addAll(
        questionRepository.findRandomQuestions(
            subject.getId(), Difficulty.EASY.name(), easyCount
        )
    );
    examQuestions.addAll(
        questionRepository.findRandomQuestions(
            subject.getId(), Difficulty.MEDIUM.name(), mediumCount
        )
    );
    examQuestions.addAll(
        questionRepository.findRandomQuestions(
            subject.getId(), Difficulty.HARD.name(), hardCount
        )
    );

    // STEP 4: Create exam entity
    Exam exam = new Exam();
    exam.setStudent(student);
    exam.setSubject(subject);
    exam.setQuestions(examQuestions);
    
    return examRepository.save(exam);
}
```

---

## 🎓 Pedagogical Benefits

### Adaptive Learning Principles

| Traditional Exam | Adaptive Exam (This System) |
|-----------------|----------------------------|
| Same questions for all students | Personalized difficulty distribution |
| Fixed difficulty | Dynamic based on mastery |
| No learning from results | Mastery updates after each exam |
| Subject-agnostic | Subject-specific tracking |
| One-size-fits-all | Zone of Proximal Development |

### Student Experience

**Beginner Student (e.g., Lance in Science)**:
- ✅ Confidence boost from many easy questions
- ✅ Gradual introduction to harder concepts
- ✅ Reduced test anxiety

**Advanced Student (e.g., Lance in Math)**:
- ✅ Intellectual challenge
- ✅ Prevents boredom
- ✅ Encourages deeper learning

**Intermediate Student (e.g., Henry in Math)**:
- ✅ Balanced assessment
- ✅ Fair representation of all skill levels
- ✅ Clear growth path

---

## 📈 Future Enhancements

### 1. Topic-Level Mastery
Instead of just "Math", track:
- Math → Algebra: 0.8
- Math → Geometry: 0.5
- Math → Calculus: 0.3

### 2. Time-Decay Factor
```java
// Questions answered 6 months ago count less
double timeFactor = Math.exp(-monthsSinceLastExam / 6.0);
newMastery = (oldMastery × timeFactor × 0.7) + (examScore × 0.3);
```

### 3. Peer Comparison
```
Your Math Mastery: 0.75
Class Average: 0.62
Percentile Rank: 78th
```

### 4. Confidence Intervals
```
Mastery: 0.75 ± 0.08 (95% confidence)
Status: Advanced (probably)
```

---

## 🏆 Summary

The adaptive exam system:

1. ✅ **Handles Cold Start**: Defaults to beginner level for new students
2. ✅ **Subject-Specific**: Lance can be advanced in Math but beginner in Science
3. ✅ **Personalized**: Henry and Lance get different exams even for the same subject
4. ✅ **Continuous Learning**: Mastery evolves with each exam
5. ✅ **Quality Distribution**: Balanced difficulty ensures comprehensive assessment
6. ✅ **Fair & Transparent**: Clear algorithm rules, no black box

**This creates a learning environment where every student is appropriately challenged!**

---

For implementation details, see `ExamGenerationService.java` and `ALGORITHM_EXPLANATION.md`.
