# Database Cleanup Script
# Run this in PowerShell to clean the database

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Database Cleanup - Remove Duplicates" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Prompt for MySQL password
Write-Host "Enter your MySQL root password (leave empty if no password):" -ForegroundColor Yellow
$password = Read-Host -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

# Find MySQL
$mysqlPath = "mysql"
if (!(Get-Command mysql -ErrorAction SilentlyContinue)) {
    $commonPaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\xampp\mysql\bin\mysql.exe"
    )
    
    foreach ($path in $commonPaths) {
        if (Test-Path $path) {
            $mysqlPath = $path
            break
        }
    }
}

try {
    Write-Host "Cleaning database..." -ForegroundColor Yellow
    
    if ($plainPassword -eq "") {
        Get-Content "cleanup-database.sql" | & $mysqlPath -u root
    } else {
        Get-Content "cleanup-database.sql" | & $mysqlPath -u root "-p$plainPassword"
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "SUCCESS! Database cleaned!" -ForegroundColor Green
        Write-Host "- Test users (Lance, Henry) removed" -ForegroundColor White
        Write-Host "- Duplicate subjects removed" -ForegroundColor White
        Write-Host "- Orphaned data cleaned" -ForegroundColor White
        Write-Host ""
        Write-Host "Now restart your application to initialize fresh data." -ForegroundColor Cyan
    }
} catch {
    Write-Host "ERROR: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
