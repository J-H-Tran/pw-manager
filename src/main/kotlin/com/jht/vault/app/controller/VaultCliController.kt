package com.jht.vault.app.controller

import com.jht.vault.app.enums.AuthResult
import com.jht.vault.domain.service.VaultAppService
import java.util.Scanner

class VaultCliController(
    private val appService: VaultAppService,
    private val scanner: Scanner
) {
    fun run() {
        print("Enter password (master or user): \n\n")
        val passphrase = scanner.nextLine().trim().toCharArray()

        when (appService.authenticate(passphrase)) {
            AuthResult.User -> println("Authenticated with user password.")
            AuthResult.Master -> handleMaster()
            AuthResult.None -> println("Authentication failed.")
        }
        passphrase.fill('\u0000')
    }

    private fun handleMaster() {
        println("Authenticated with master password.")
        if (appService.shouldCreateUserPassword()) {
            print("Create a new user password (min 8 chars): ")
            val newUserPassword = scanner.nextLine().toCharArray()

            if (appService.saveUserPassword(newUserPassword)) {
                println("User password saved.")
            } else {
                println("Password too short.")
            }
            newUserPassword.fill('\u0000')
        } else {
            println("User password already exists.")
        }
    }
}