package com.jht.vault.util

import java.security.SecureRandom

object PasswordUtils {
    fun generateRandomPassword(length: Int = 16): CharArray {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+[]{}|;:,.<>?/"
        val rnd = SecureRandom()
        return CharArray(length) { chars[rnd.nextInt(chars.length)] }
    }
}