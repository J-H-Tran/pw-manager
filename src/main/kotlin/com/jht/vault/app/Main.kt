package com.jht.vault.app

import com.jht.vault.app.controller.VaultCliController
import com.jht.vault.app.db.DatabaseHelper
import com.jht.vault.app.db.DatabaseInitializer
import com.jht.vault.repository.MasterPasswordRepository
import com.jht.vault.repository.UserPasswordRepository
import com.jht.vault.service.MasterPasswordService
import com.jht.vault.service.UserPasswordService
import com.jht.vault.service.VaultAppService
import java.util.Scanner

fun main() {
    val dbPath = "src/main/resources/data/vault.db"
    DatabaseInitializer.initialize(dbPath)
    DatabaseHelper.open(dbPath, "").use { conn ->
        val masterService = MasterPasswordService(MasterPasswordRepository(conn))
        val userService = UserPasswordService(UserPasswordRepository(conn))
        val appService = VaultAppService(masterService, userService)
        VaultCliController(appService, Scanner(System.`in`)).run()
        DatabaseHelper.close(conn)
    }
}