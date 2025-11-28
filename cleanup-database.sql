-- ============================================
-- Clean Database - Drop and Recreate
-- ============================================
-- Run this in MySQL to completely reset your database

-- Drop the entire database
DROP DATABASE IF EXISTS examdevelopment;

-- Recreate the database
CREATE DATABASE examdevelopment;

-- Use the new database
USE examdevelopment;

-- Success message
SELECT 'Database successfully dropped and recreated!' AS Status;

SELECT 'Cleanup completed successfully!' AS Status;
