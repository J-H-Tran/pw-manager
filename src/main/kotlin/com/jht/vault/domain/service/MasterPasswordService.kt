package com.jht.vault.domain.service

import com.jht.vault.domain.repository.MasterPasswordRepository
import com.jht.vault.util.CryptoUtils
import com.jht.vault.util.PasswordUtils

class MasterPasswordService(
    private val repository: MasterPasswordRepository
) {
    fun generateAndStorePasswords(count: Int): List<CharArray> {
        val passwords = List(count) { PasswordUtils.generateRandomPassword() }
        passwords.forEach { pw ->
            println(pw)
            val hash = CryptoUtils.hashPassword(pw)
            repository.saveHash(hash)
            pw.fill('\u0000')
        }
        return passwords
    }

    fun authenticate(input: CharArray): Boolean {
        val hashes = repository.getAllHashes()
        return hashes.any { CryptoUtils.verifyPassword(input, it) }
    }

    fun rotatePasswords(count: Int): List<CharArray> {
        repository.deleteAll()
        return generateAndStorePasswords(count)
    }
}