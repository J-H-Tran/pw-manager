package com.jht.vault

import com.jht.vault.db.DatabaseHelper
import com.jht.vault.db.DatabaseInitializer
import com.jht.vault.domain.repository.MasterPasswordRepository
import com.jht.vault.domain.repository.UserPasswordRepository
import com.jht.vault.domain.service.MasterPasswordService
import com.jht.vault.domain.service.UserPasswordService
import com.jht.vault.util.CryptoUtils
import java.util.Scanner

fun main() {
    val dbPath = "src/main/resources/data/vault.db"
    DatabaseInitializer.initialize(dbPath)

    DatabaseHelper.open(dbPath, "").use { conn ->
        val masterRepo = MasterPasswordRepository(conn)
        val userRepo = UserPasswordRepository(conn)
        val masterService = MasterPasswordService(masterRepo)
        val userService = UserPasswordService(userRepo)

        val scanner = Scanner(System.`in`)

        print("Enter password (master or user): \n\n")
        val passphrase = scanner.nextLine().trim()
        val passphraseChar = passphrase.toCharArray()
        val passphraseByte = passphrase.toByteArray(Charsets.UTF_8)

        // Fetch the single master password record
        val masterRecord = masterService.getMasterPassword()
        if (masterRecord == null) {
            println("No master password found.")
            passphraseChar.fill('\u0000')
            return
        }

        val key = CryptoUtils.deriveKey(passphraseChar, masterRecord.salt, 32)
        val aad = masterRecord.aad ?: ByteArray(0)

        println("Derived key length: ${key.size}") // Should be 16, 24, or 32
        println("Salt: ${masterRecord.salt.joinToString()}")
        println("Passphrase length: ${passphrase.length}")

        val decryptedString = CryptoUtils.decryptAESGCM(key, aad, masterRecord.encryptedPassword, masterRecord.iv)
        println("Decrypted password: " + String(decryptedString, Charsets.UTF_8))

        val userAuth = false

        //second key length check
        println("(2) Derived key length: ${key.size}") // Should be 16, 24, or 32

        val masterAuth = if (!userAuth) masterService.authenticate(
            passphraseByte,
            key,
            aad,
            masterRecord.encryptedPassword,
            masterRecord.iv,
        ) else false

        if (userAuth) {
            println("Authenticated with user password.")
        } else if (masterAuth) {
            println("Authenticated with master password.")
            print("Create a new user password (min 8 chars): ")
            val newUserPassword = scanner.nextLine().toCharArray()
            if (newUserPassword.size < 8) {
                println("Password too short.")
            } else {
                userService.savePassword(newUserPassword)
                println("User password saved.")
            }
            newUserPassword.fill('\u0000')
        } else {
            println("Authentication failed.")
        }

        passphraseChar.fill('\u0000')
        DatabaseHelper.close(conn)
    }
}