package com.jht.vault

import com.jht.vault.db.DatabaseHelper
import com.jht.vault.db.DatabaseInitializer
import com.jht.vault.domain.repository.MasterPasswordRepository
import com.jht.vault.domain.service.MasterPasswordService
import java.util.Scanner

fun main() {
    val dbPath = "src/main/resources/data/vault.db"
    DatabaseInitializer.initialize(dbPath)

    DatabaseHelper.open(dbPath, "").use { conn ->
        val repository = MasterPasswordRepository(conn)
        val service = MasterPasswordService(repository)

        println("Generated master passwords (save these!):")
        service.generateAndStorePasswords(4)

        val scanner = Scanner(System.`in`)
        print("Enter master password: ")
        val input = scanner.nextLine().toCharArray()

        if (service.authenticate(input)) {
            println("Authentication successful. Rotating master passwords...")
            service.rotatePasswords(4)
        } else {
            println("Authentication failed.")
        }
        input.fill('\u0000')
        DatabaseHelper.close(conn)
    }
}