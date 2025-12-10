package com.jht.vault.service

import com.jht.vault.app.enums.AuthResult
import com.jht.vault.util.CryptoUtils
import org.slf4j.LoggerFactory
import javax.crypto.AEADBadTagException

class VaultAppService(
    private val masterService: MasterPasswordService,
    private val userService: UserPasswordService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun authenticate(passphrase: CharArray): AuthResult {
        val userAuth = userService.authenticate(passphrase)
        if (userAuth) return AuthResult.User

        val masterRecord = masterService.getMasterPassword() ?: return AuthResult.None
        val key = CryptoUtils.deriveKey(passphrase, masterRecord.salt)
        val aad = masterRecord.aad ?: ByteArray(0)

        return try {
            val masterAuth = masterService.authenticate(
                passphrase.concatToString().toByteArray(Charsets.UTF_8),
                key,
                aad,
                masterRecord.encryptedPassword,
                masterRecord.iv
            )
            if (masterAuth) AuthResult.Master else AuthResult.None
        } catch (ex: AEADBadTagException) {
            logger.error("Master Password was incorrect.", ex)
            AuthResult.None
        }
    }

    fun shouldCreateUserPassword(): Boolean = userService.isEmpty()

    fun saveUserPassword(password: CharArray): Boolean {
        if (password.size < 8) return false
        userService.savePassword(password)
        return true
    }
}