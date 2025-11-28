package com.thesis.exam.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for encrypting and decrypting verification tokens
 * This hides the raw UUID from being visible in email links
 */
public class TokenEncryptor {
    
    // Secret key for encryption (in production, move to config/env variable)
    private static final String SECRET_KEY = "ExamSystemSecretKey2024!!"; // Must be 16, 24, or 32 bytes
    private static final String ALGORITHM = "AES";
    
    /**
     * Encrypt a token using AES encryption and encode as URL-safe Base64
     */
    public static String encrypt(String plainToken) {
        try {
            // Create AES key from secret
            SecretKeySpec secretKey = createKey();
            
            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            // Encrypt the token
            byte[] encryptedBytes = cipher.doFinal(plainToken.getBytes(StandardCharsets.UTF_8));
            
            // Encode as URL-safe Base64 (replace + with -, / with _)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting token", e);
        }
    }
    
    /**
     * Decrypt a token from URL-safe Base64 encoded AES encrypted data
     */
    public static String decrypt(String encryptedToken) {
        try {
            // Create AES key from secret
            SecretKeySpec secretKey = createKey();
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            // Decode from URL-safe Base64
            byte[] encryptedBytes = Base64.getUrlDecoder().decode(encryptedToken);
            
            // Decrypt the token
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting token", e);
        }
    }
    
    /**
     * Create AES encryption key from the secret key string
     */
    private static SecretKeySpec createKey() {
        try {
            // Hash the secret key to ensure it's the right length
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            
            // Use only first 128 bits (16 bytes) for AES-128
            byte[] truncatedKey = new byte[16];
            System.arraycopy(key, 0, truncatedKey, 0, 16);
            
            return new SecretKeySpec(truncatedKey, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error creating encryption key", e);
        }
    }
}
