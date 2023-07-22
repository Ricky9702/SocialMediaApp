package com.example.h2ak.utils;

import com.lambdaworks.crypto.SCryptUtil;

public class PasswordHashing {
    public static String hashPassword(String password) {
        return SCryptUtil.scrypt(password, 16, 16, 16);
    }
    public static boolean verifyPassword(String password, String hashedPassword) {
        return SCryptUtil.check(password, hashedPassword);
    }
}
