# Email Verification Setup Guide

## ✅ Email Verification Implemented!

I've added complete email verification functionality to your system.

### 🎯 Features Added:

1. **Email Verification on Registration**
   - Users must verify email before login
   - Verification link sent automatically
   - Token expires in 24 hours

2. **Email Templates**:
   - Verification email with clickable link
   - Welcome email after verification
   - Password reset email (future feature)

3. **New Pages**:
   - `register-success.html` - Shows after registration
   - `verify-email.html` - Shows verification status

4. **Enhanced Security**:
   - Users can't login without email verification
   - Tokens expire after 24 hours
   - Prevention of duplicate registrations

### 📧 Setting Up Email (IMPORTANT!)

To send emails, you need to configure SMTP settings in `application.properties`:

#### Option 1: Gmail (Recommended for Testing)

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Create an App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate new app password
   
3. **Update `application.properties`**:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-digit-app-password
   ```

#### Option 2: Other Email Services

**Outlook/Hotmail**:
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

**Yahoo**:
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

### 🔄 How It Works:

```
1. User registers → System creates account with emailVerified=false
2. System generates unique verification token
3. Email sent with link: http://localhost:8080/verify?token=xxx
4. User clicks link → /verify endpoint validates token
5. Token valid → emailVerified=true, welcome email sent
6. User can now login
```

### 🧪 Testing Without Email (Development):

If you don't want to set up email during development, you can:

1. **Check Console Logs**: The token will be printed (if you add logging)
2. **Manually verify in database**: Set `emailVerified=true` directly
3. **Use H2 Console**: Update records manually

### 📝 What Was Added:

**Models**:
- `Student` & `Teacher`: Added `emailVerified`, `verificationToken`, `tokenExpiryDate`

**Services**:
- `EmailService`: Sends verification, welcome, and password reset emails
- `AuthService`: Enhanced with `verifyEmail()` method

**Controllers**:
- `AuthController`: Added `/verify` endpoint

**Templates**:
- `register-success.html`: Post-registration page
- `verify-email.html`: Verification result page

**Dependencies**:
- `spring-boot-starter-mail`: Added to pom.xml

### 🚀 Next Steps:

1. Configure email settings in `application.properties`
2. Restart application
3. Test registration flow
4. Check email inbox for verification link
5. Click link and verify

**Note**: For production, consider using professional email services like SendGrid, Amazon SES, or Mailgun for better deliverability.
