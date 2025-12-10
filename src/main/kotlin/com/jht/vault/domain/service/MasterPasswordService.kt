package com.jht.vault.domain.service

import com.jht.vault.domain.repository.EncryptedEntry
import com.jht.vault.domain.repository.MasterPasswordRepository
import com.jht.vault.util.CryptoUtils
import com.jht.vault.util.PasswordUtils

class MasterPasswordService(
    private val repository: MasterPasswordRepository
) {
    fun generateAndSaveBackupMasterPassword(
        aad: ByteArray?,
        algorithm: String = "AES-GCM"
    ) {
        val password = PasswordUtils.generateRandomPassword()
        println("Backup Master Password: ${String(password)}")
        val salt = CryptoUtils.generateSalt()
        val key = CryptoUtils.deriveKey(password, salt)
        val (encrypted, iv) = CryptoUtils.encryptAESGCM(String(password).toByteArray(Charsets.UTF_8), key, aad)
        repository.deleteAll() // Ensure only one master password exists
        repository.saveEncryptedPassword(encrypted, iv, aad, salt, algorithm)
    }

    fun getMasterPassword(): EncryptedEntry? {
        return repository.getLatestMasterPassword()
    }

    fun authenticate(
        passphraseByte: ByteArray,
        key: ByteArray,
        aad: ByteArray?,
        encryptedPassword: ByteArray,
        iv: ByteArray,
    ): Boolean {
        val decrypted = CryptoUtils.decryptAESGCM(key, aad ?: ByteArray(0), encryptedPassword, iv)
        return decrypted.contentEquals(passphraseByte)
    }

    fun deleteMasterPassword() {
        repository.deleteAll()
    }
}