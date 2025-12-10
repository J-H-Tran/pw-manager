package com.jht.vault

import java.security.SecureRandom
import java.util.UUID

object PasswordManager {
    private var userPassword: String? = null
    private var backupPasswords: MutableList<String> = mutableListOf()

    init {
        if (backupPasswords.isEmpty()) {
            refreshBackupPasswords()
        }
    }

    fun authenticate(input: String): Boolean {
        return when {
            userPassword != null && input == userPassword -> true
            backupPasswords.contains(input) -> {
                refreshBackupPasswords()
                true
            }
            else -> false
        }
    }

    fun setUserPassword(newPassword: String) {
        userPassword = newPassword
    }

    fun refreshBackupPasswords() {
        backupPasswords = generateBackupPasswords()
    }

    fun generateBackupPasswords(): MutableList<String> {
        val random = SecureRandom()
        return MutableList(4) { UUID.randomUUID().toString().substring(0, 8) }
    }

    fun hasUserPassword(): Boolean = userPassword != null

    fun getBackupPasswords(): List<String> = backupPasswords
}