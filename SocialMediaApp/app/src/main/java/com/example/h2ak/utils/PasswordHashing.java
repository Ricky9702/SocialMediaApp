package com.example.h2ak.utils;


import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHashing {
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
    public static boolean verifyPassword(String password, String bcryptHashString) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString);
        return result.verified == true;
    }
}
