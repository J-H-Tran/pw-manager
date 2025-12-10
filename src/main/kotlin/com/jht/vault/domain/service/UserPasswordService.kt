package com.jht.vault.domain.service

import com.jht.vault.domain.repository.UserPasswordRepository
import com.jht.vault.util.CryptoUtils
import com.jht.vault.util.PasswordUtils

class UserPasswordService(
    private val repository: UserPasswordRepository
) {
    fun savePassword(password: CharArray) {
        val hash = CryptoUtils.hashPassword(password)
        repository.saveHash(hash)
        password.fill('\u0000')
    }

    fun authenticate(input: CharArray): Boolean {
        val hash = repository.getHash() ?: ""
        return CryptoUtils.verifyPassword(input, hash)
    }
}