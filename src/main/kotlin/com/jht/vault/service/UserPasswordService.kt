package com.jht.vault.service

import com.jht.vault.repository.UserPasswordRepository
import com.jht.vault.util.CryptoUtils

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

    fun isEmpty(): Boolean = repository.isEmpty()
}