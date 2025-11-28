# Quick Start Guide

## 🚀 Running the Application

### Option 1: Using IDE (Recommended for Development)

1. **Open the project in your IDE** (IntelliJ IDEA, Eclipse, VS Code with Java extensions)
2. **Locate `ExamApplication.java`** in `src/main/java/com/thesis/exam/`
3. **Right-click** → **Run 'ExamApplication'**
4. **Open browser**: [http://localhost:8080](http://localhost:8080)

### Option 2: Using Maven (If Maven is installed)

**PowerShell/Command Prompt:**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment
mvn spring-boot:run
```

**Note:** Run these as **two separate commands** or use `&&`:
```powershell
cd C:\Users\SET-PC29\ExamDevelopment && mvn spring-boot:run
```

### Option 3: Using Maven Wrapper (No Maven installation required)

**Windows PowerShell:**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment
.\mvnw.cmd spring-boot:run
```

**Windows Command Prompt (cmd):**
```cmd
cd C:\Users\SET-PC29\ExamDevelopment
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
cd /path/to/ExamDevelopment
./mvnw spring-boot:run
```

---

## ⚠️ Common Command Errors

### ❌ Wrong (Will cause error):
```powershell
cd C:\Users\SET-PC29\ExamDevelopment mvn spring-boot:run
```

### ✅ Correct Options:

**Option A - Two separate commands:**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment
mvn spring-boot:run
```

**Option B - Single line with &&:**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment && mvn spring-boot:run
```

**Option C - Using semicolon (PowerShell):**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment; mvn spring-boot:run
```

---

## 📌 First Time Setup

### Check if Maven is Installed:
```powershell
mvn -version
```

If you see version info, Maven is installed. If not:

1. **Install Maven**:
   - Download from: https://maven.apache.org/download.cgi
   - Add to PATH environment variable
   
2. **Or use IDE** (Most IDEs have built-in Maven support):
   - IntelliJ IDEA (Community/Ultimate)
   - Eclipse with Spring Tools
   - VS Code with Extension Pack for Java

3. **Or use Maven Wrapper** (Already included in project):
   ```powershell
   cd C:\Users\SET-PC29\ExamDevelopment
   .\mvnw.cmd spring-boot:run
   ```

---

## 🌐 Access Points

After starting the application:

| Resource | URL |
|----------|-----|
| Home Page | http://localhost:8080 |
| Upload Questions | http://localhost:8080/question/upload |
| View Questions | http://localhost:8080/question/list |
| Generate Exam | http://localhost:8080/exam/generate |
| H2 Database Console | http://localhost:8080/h2-console |

### H2 Console Credentials:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

---

## 🧪 Testing the Algorithm

### Scenario 1: Cold Start (New Student)
1. Go to "Generate Exam"
2. Select: **Lance** or **Henry** (both are new students)
3. Choose any subject
4. Observe: You'll get a **balanced distribution** (70% Easy, 20% Medium, 10% Hard)

### Scenario 2: After Building History
1. Complete an exam and submit a score (e.g., 0.9 for high performance)
2. Generate another exam for the same student in the same subject
3. Observe: You'll get **more hard questions** (Advanced distribution)

### Scenario 3: Subject-Specific Mastery
1. Submit high score (0.9) for Lance in Mathematics
2. Submit low score (0.2) for Lance in Science
3. Generate exam for Lance in Math → Mostly HARD questions
4. Generate exam for Lance in Science → Mostly EASY questions

---

## 📝 Sample Workflow

```
1. Teacher uploads questions → /question/upload
2. System tags questions with difficulty (EASY/MEDIUM/HARD)
3. Student generates exam → /exam/generate
4. System checks mastery → Selects appropriate difficulty mix
5. Student completes exam → Submit score
6. System updates mastery → Ready for next exam
```

---

## 🛠️ Troubleshooting

### Issue: "mvn: command not found" or "mvn is not recognized"

**Solution 1 - Use Maven Wrapper:**
```powershell
cd C:\Users\SET-PC29\ExamDevelopment
.\mvnw.cmd spring-boot:run
```

**Solution 2 - Install Maven:**
1. Download from https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Restart PowerShell/CMD

**Solution 3 - Use IDE:**
Just open the project in IntelliJ/Eclipse and click Run!

### Port 8080 Already in Use
Edit `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Database Issues
The H2 database is in-memory, so data resets on restart. To persist data, change to file-based:
```properties
spring.datasource.url=jdbc:h2:file:./data/examdb
```

### No Questions Available
Run the application once - the `DataInitializer` will auto-create sample data.

---

## 💡 Pro Tips

1. **View SQL Queries**: Add to `application.properties`:
   ```properties
   spring.jpa.show-sql=true
   ```

2. **Enable Debug Logging**:
   ```properties
   logging.level.com.thesis.exam=DEBUG
   ```

3. **Change Database**: Replace H2 with MySQL/PostgreSQL by updating dependencies in `pom.xml`

---

## 🎯 Quick Command Reference

| Task | Command |
|------|---------|
| Navigate to project | `cd C:\Users\SET-PC29\ExamDevelopment` |
| Run with Maven | `mvn spring-boot:run` |
| Run with Maven Wrapper | `.\mvnw.cmd spring-boot:run` |
| Clean and run | `mvn clean spring-boot:run` |
| Build JAR file | `mvn clean package` |
| Run JAR file | `java -jar target\exam-0.0.1-SNAPSHOT.jar` |

---

Need help? Check the main **README.md** for full documentation!
