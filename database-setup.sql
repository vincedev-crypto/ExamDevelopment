-- ============================================
-- Exam Development System - Database Setup
-- ============================================
-- Run this script in MySQL to create the database
-- You can use MySQL Workbench, phpMyAdmin, or command line

-- Create the database
CREATE DATABASE IF NOT EXISTS examdevelopment 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE examdevelopment;

-- Grant privileges (if needed)
-- GRANT ALL PRIVILEGES ON examdevelopment.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- Verify the database was created
SHOW DATABASES LIKE 'examdevelopment';

-- The tables will be automatically created by Hibernate when you run the Spring Boot application
-- with spring.jpa.hibernate.ddl-auto=update

SELECT 'Database examdevelopment created successfully!' AS Status;
