package com.example.Backend.Services;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {
    //hash a plaintext password
    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    //verify a plaintext password against a hashed password
    public static boolean checkPassword(String plainTextPassword, String hashedPassword){
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
