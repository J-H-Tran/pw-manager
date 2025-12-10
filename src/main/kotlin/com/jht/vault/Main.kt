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

        // Fetch the single master password record
        val masterRecord = masterService.getMasterPassword()
        if (masterRecord == null) {
            println("No master password found.")
            return
        }

        val key = CryptoUtils.deriveKey(passphraseChar, masterRecord.salt)
        val aad = masterRecord.aad ?: ByteArray(0)

        val userAuth = userService.authenticate(passphraseChar)
        val masterAuth =
            if (!userAuth) masterService.authenticate(
                passphrase.toByteArray(Charsets.UTF_8),
                key,
                aad,
                masterRecord.encryptedPassword,
                masterRecord.iv,
            ) else false

        if (userAuth) {
            println("Authenticated with user password.")
        } else if (masterAuth) {
            println("Authenticated with master password.")
            if (userRepo.getHash() == null) {
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
                println("User password already exists.")
            }
        } else {
            println("Authentication failed.")
        }

        DatabaseHelper.close(conn)
    }
}