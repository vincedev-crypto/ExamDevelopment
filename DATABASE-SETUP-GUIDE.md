# Exam Development System - MySQL Database Setup Guide

## Prerequisites
- MySQL Server 8.0 or higher installed
- MySQL running on localhost:3306
- Root user access (or another MySQL user with database creation privileges)

## Step 1: Create the Database

### Option A: Using MySQL Command Line
```bash
# Open MySQL command line
mysql -u root -p

# Enter your MySQL root password when prompted
# Then run:
CREATE DATABASE IF NOT EXISTS examdevelopment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### Option B: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your local MySQL server
3. Click on "Create a new schema" button (cylinder with plus icon)
4. Name: `examdevelopment`
5. Character Set: `utf8mb4`
6. Collation: `utf8mb4_unicode_ci`
7. Click "Apply"

### Option C: Using the SQL Script
```bash
mysql -u root -p < database-setup.sql
```

## Step 2: Update Database Password (if needed)

If your MySQL root user has a password, update `application.properties`:

```properties
spring.datasource.password=your_mysql_password
```

**Current configuration:**
- Database Name: `examdevelopment`
- Host: `localhost:3306`
- Username: `root`
- Password: (empty - update if you have a password)

## Step 3: Run the Application

The tables will be automatically created by Hibernate when you start the Spring Boot application.

```bash
.\mvnw.cmd spring-boot:run
```

## Database Tables Created Automatically

When you run the application, these tables will be created:
- `student` - Student user accounts
- `teacher` - Teacher user accounts
- `subject` - Academic subjects
- `exam` - Generated exams
- `exam_assignment` - Assignments of exams to students
- `question` - Exam questions
- `student_subject_mastery` - Student performance tracking

## Data Persistence

✅ **Your data will now persist across application restarts!**

- User accounts (students/teachers) are saved permanently
- Email verification status is preserved
- Exam assignments and results are kept
- No need to register every time you run the app

## Troubleshooting

### Error: "Access denied for user 'root'@'localhost'"
- Update the password in `application.properties`
- Or create a new MySQL user:
```sql
CREATE USER 'examuser'@'localhost' IDENTIFIED BY 'exampass123';
GRANT ALL PRIVILEGES ON examdevelopment.* TO 'examuser'@'localhost';
FLUSH PRIVILEGES;
```

### Error: "Communications link failure"
- Make sure MySQL service is running
- Check if MySQL is listening on port 3306

### Error: "Unknown database 'examdevelopment'"
- Run the database creation script first
- Or manually create the database in MySQL Workbench

## Verifying the Setup

After running the application, you can verify the tables were created:

```sql
USE examdevelopment;
SHOW TABLES;
```

You should see all the tables listed above.
