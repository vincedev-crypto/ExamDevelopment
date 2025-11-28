# Adaptive Exam Generation System

## 📚 Overview
This is a **Thesis Project** implementing an intelligent exam generation system that adapts question difficulty based on individual student performance. Built with **Java Spring Boot** and **Maven**.

---

## 🎯 Key Concepts & Algorithm Insights

### **1. How Difficulty is Set When Teachers Upload Questions**
- **Initial Tagging**: When a teacher uploads a question, they **manually assign** a difficulty level:
  - `EASY` (Level 1)
  - `MEDIUM` (Level 2)
  - `HARD` (Level 3)

- **No Automatic Arrangement**: The system does NOT automatically sort questions ascending/descending unless specified. Instead, it intelligently **selects** questions based on student proficiency.

### **2. The Cold Start Problem (No History)**
**What happens when a new student like Lance or Henry first uses the system?**

- **Default Assumption**: The system treats them as **Intermediate** (mastery score = 0.0 or no data).
- **Balanced Distribution**: First exam uses a standard mix:
  - 30% Easy
  - 50% Medium  
  - 20% Hard
- **Purpose**: This "diagnostic test" establishes a baseline to build future history.

### **3. Student-Specific & Subject-Specific Mastery**
**Example Scenario:**
- **Lance**: Great at Math (High Mastery), Weak at Science (Low Mastery)
- **Henry**: Weak at Math (Low Mastery), Good at Programming (High Mastery)

**Algorithm Logic:**
```
IF mastery_score < 0.3 (Beginner):
    → 70% Easy, 20% Medium, 10% Hard
ELSE IF mastery_score < 0.7 (Intermediate):
    → 30% Easy, 50% Medium, 20% Hard
ELSE (Advanced):
    → 10% Easy, 30% Medium, 60% Hard
```

**Tracking**: The `StudentSubjectMastery` table stores **per-student, per-subject** scores, so:
- Lance gets HARD Math questions but EASY Science questions
- Henry gets EASY Math questions but HARD Programming questions

### **4. Distribution & Quality Control**
- **Blueprint Approach**: Follows a "Table of Specifications"
  - Example: "Get 50 questions total from Subject X: 20% Easy, 60% Medium, 20% Hard"
  
- **Quality Metrics** (Future Enhancement):
  - **Discrimination Index**: If high-performing students fail a question and low-performing students pass it, it's flagged as "poor quality"
  - **Dynamic Re-tagging**: Questions initially marked "Easy" but with 90% fail rate get reclassified as "Hard"

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17, Spring Boot 3.2.0 |
| Build Tool | Maven |
| Database | H2 (In-memory, for development) |
| Frontend | Thymeleaf, HTML5, CSS3, JavaScript |
| ORM | Spring Data JPA / Hibernate |

---

## 📁 Project Structure

```
ExamDevelopment/
├── src/
│   ├── main/
│   │   ├── java/com/thesis/exam/
│   │   │   ├── controller/
│   │   │   │   ├── ExamController.java
│   │   │   │   ├── QuestionController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── model/
│   │   │   │   ├── Difficulty.java (Enum)
│   │   │   │   ├── Student.java
│   │   │   │   ├── Subject.java
│   │   │   │   ├── Question.java
│   │   │   │   ├── Exam.java
│   │   │   │   └── StudentSubjectMastery.java
│   │   │   ├── repository/
│   │   │   │   ├── QuestionRepository.java
│   │   │   │   ├── ExamRepository.java
│   │   │   │   ├── StudentRepository.java
│   │   │   │   ├── SubjectRepository.java
│   │   │   │   └── StudentSubjectMasteryRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ExamGenerationService.java ⭐ (Core Algorithm)
│   │   │   │   ├── QuestionService.java
│   │   │   │   ├── StudentService.java
│   │   │   │   └── SubjectService.java
│   │   │   ├── DataInitializer.java (Sample Data)
│   │   │   └── ExamApplication.java (Main)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/style.css
│   │       │   └── js/script.js
│   │       └── templates/
│   │           ├── index.html
│   │           ├── upload-question.html
│   │           ├── question-list.html
│   │           ├── generate-exam.html
│   │           └── exam-result.html
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**

### Installation & Running

1. **Clone/Navigate to the project**:
   ```bash
   cd c:\Users\SET-PC29\ExamDevelopment
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - Main App: [http://localhost:8080](http://localhost:8080)
   - H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: `password`

---

## 📊 Sample Data

The system auto-initializes with:

### **Students**:
- Lance (lance@example.com)
- Henry (henry@example.com)

### **Subjects**:
- Mathematics
- Science
- Programming

### **Sample Questions**:
- 9 questions total (3 per subject)
- Each subject has 1 Easy, 1 Medium, 1 Hard question

---

## 🔄 Workflow

### **For Teachers**:
1. Navigate to **Upload Questions**
2. Enter question content, select subject, difficulty, and correct answer
3. Submit → Question saved to database

### **For Exam Generation**:
1. Navigate to **Generate Exam**
2. Select Student and Subject
3. Specify number of questions (default: 20)
4. Click **Generate** → System:
   - Checks student's mastery level in that subject
   - Applies difficulty distribution algorithm
   - Randomly selects questions matching the distribution
   - Displays the generated exam

### **For Updating Mastery**:
1. After exam completion, enter the score (0.0 to 1.0)
2. Submit → System updates the student's mastery using weighted average:
   ```
   New Mastery = (Old Mastery × 0.7) + (Exam Score × 0.3)
   ```

---

## 🧮 Core Algorithm (ExamGenerationService)

```java
// Pseudo-code
public Exam generateExam(Student student, Subject subject, int totalQuestions) {
    // 1. Fetch mastery score (default 0.0 if new student)
    double mastery = getMastery(student, subject);
    
    // 2. Determine distribution based on mastery
    if (mastery < 0.3) {
        // Beginner
        easy = 70%, medium = 20%, hard = 10%
    } else if (mastery < 0.7) {
        // Intermediate
        easy = 30%, medium = 50%, hard = 20%
    } else {
        // Advanced
        easy = 10%, medium = 30%, hard = 60%
    }
    
    // 3. Fetch random questions matching distribution
    questions = fetchQuestions(subject, easyCount, mediumCount, hardCount);
    
    // 4. Create and save exam
    return new Exam(student, subject, questions);
}
```

---

## 🎨 Features

✅ **Adaptive Difficulty**: Questions tailored to individual student mastery  
✅ **Cold Start Handling**: Balanced distribution for new students  
✅ **Subject-Specific Tracking**: Per-student, per-subject mastery scores  
✅ **Dynamic Mastery Updates**: Scores evolve based on performance  
✅ **Teacher Portal**: Easy question upload with difficulty tagging  
✅ **Responsive UI**: Clean, modern interface with gradient design  

---

## 🔮 Future Enhancements

1. **Question Quality Metrics**:
   - Implement Discrimination Index
   - Auto-reclassify questions based on performance data

2. **Advanced Algorithms**:
   - Item Response Theory (IRT)
   - Computerized Adaptive Testing (CAT)

3. **Analytics Dashboard**:
   - Student progress charts
   - Question performance heatmaps

4. **Multi-format Questions**:
   - Multiple choice
   - True/False
   - Essay questions

5. **Production Database**:
   - Replace H2 with PostgreSQL/MySQL

---

## 👥 Authors
**Thesis Project Team**  
© 2025 Adaptive Exam System

---

## 📝 License
This project is developed for academic/thesis purposes.
