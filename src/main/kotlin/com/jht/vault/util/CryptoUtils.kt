package com.jht.vault.util

import de.mkammerer.argon2.Argon2Factory
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    fun hashPassword(password: CharArray): String {
        val argon2 = Argon2Factory.create()
        return argon2.hash(2, 65536, 1, password, StandardCharsets.UTF_8)
    }

    fun generateSalt(length: Int = 16): ByteArray {
        val salt = ByteArray(length)
        java.security.SecureRandom().nextBytes(salt)
        return salt
    }

    fun verifyPassword(
        password: CharArray,
        hash: String
    ): Boolean {
        val argon2 = Argon2Factory.create()
        return argon2.verify(hash, password, StandardCharsets.UTF_8)
    }

    fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        keyLength: Int = 32
    ): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password, salt, 65536, keyLength * 8)
        val key = factory.generateSecret(spec).encoded
        return key
    }

    fun encryptAESGCM(
        plain: ByteArray,
        key: ByteArray,
        aad: ByteArray?
    ): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        cipher.updateAAD(aad)
        val encrypted = cipher.doFinal(plain)
        key.fill(0)
        return Pair(encrypted, iv) // encrypted first, iv second
    }

    fun decryptAESGCM(
        key: ByteArray,
        aad: ByteArray,
        encrypted: ByteArray,
        iv: ByteArray,
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        cipher.updateAAD(aad)
        return cipher.doFinal(encrypted)
    }
}