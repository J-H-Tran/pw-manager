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

    fun deriveKey(password: CharArray, salt: ByteArray, keyLength: Int = 32): ByteArray {
        val spec = PBEKeySpec(password, salt, 65536, keyLength * 8)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec).encoded
        password.fill('\u0000')
        return key
    }

    fun encryptAESGCM(plain: ByteArray, key: ByteArray, aad: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        cipher.updateAAD(aad)
        val encrypted = cipher.doFinal(plain)
        key.fill(0) // zero key
        return Pair(iv, encrypted)
    }

    fun decryptAESGCM(iv: ByteArray, encrypted: ByteArray, key: ByteArray, aad: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        cipher.updateAAD(aad)
        return cipher.doFinal(encrypted)
    }
}