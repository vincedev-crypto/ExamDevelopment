# Exam Development System - Database Setup Script
# This script creates the MySQL database for the Exam Development System

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Exam Development System - Database Setup" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Check if MySQL is accessible
Write-Host "Checking MySQL connection..." -ForegroundColor Yellow

$mysqlPath = "mysql"

# Try to find MySQL in common locations if not in PATH
if (!(Get-Command mysql -ErrorAction SilentlyContinue)) {
    $commonPaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\xampp\mysql\bin\mysql.exe",
        "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe"
    )
    
    foreach ($path in $commonPaths) {
        if (Test-Path $path) {
            $mysqlPath = $path
            Write-Host "Found MySQL at: $mysqlPath" -ForegroundColor Green
            break
        }
    }
}

# Prompt for MySQL password
Write-Host ""
Write-Host "Enter your MySQL root password (leave empty if no password):" -ForegroundColor Yellow
$password = Read-Host -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

# Create SQL commands
$sqlCommands = @"
CREATE DATABASE IF NOT EXISTS examdevelopment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE examdevelopment;
SHOW DATABASES LIKE 'examdevelopment';
SELECT 'Database examdevelopment created successfully!' AS Status;
"@

# Save to temporary file
$tempSqlFile = "$env:TEMP\examdev_setup.sql"
$sqlCommands | Out-File -FilePath $tempSqlFile -Encoding UTF8

try {
    # Execute SQL
    Write-Host ""
    Write-Host "Creating database 'examdevelopment'..." -ForegroundColor Yellow
    
    if ($plainPassword -eq "") {
        & $mysqlPath -u root < $tempSqlFile
    } else {
        & $mysqlPath -u root "-p$plainPassword" < $tempSqlFile
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "============================================" -ForegroundColor Green
        Write-Host "SUCCESS! Database created successfully!" -ForegroundColor Green
        Write-Host "============================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Cyan
        Write-Host "1. Update application.properties if you have a MySQL password" -ForegroundColor White
        Write-Host "2. Run the Spring Boot application: .\mvnw.cmd spring-boot:run" -ForegroundColor White
        Write-Host "3. Tables will be created automatically on first run" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "ERROR: Failed to create database" -ForegroundColor Red
        Write-Host "Please check your MySQL credentials and try again" -ForegroundColor Red
        Write-Host ""
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting:" -ForegroundColor Yellow
    Write-Host "- Make sure MySQL is installed and running" -ForegroundColor White
    Write-Host "- Verify your MySQL root password" -ForegroundColor White
    Write-Host "- Check if MySQL service is started" -ForegroundColor White
    Write-Host ""
} finally {
    # Clean up temp file
    if (Test-Path $tempSqlFile) {
        Remove-Item $tempSqlFile -Force
    }
}

Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
