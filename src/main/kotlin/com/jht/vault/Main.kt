package com.jht.vault

import com.jht.vault.db.DatabaseHelper
import com.jht.vault.db.DatabaseInitializer
import java.util.Scanner

fun main() {
    val dbPath = "src/main/resources/data/vault.db"
    DatabaseInitializer.initialize(dbPath)

    DatabaseHelper.open(dbPath, "").use { conn ->
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT COUNT(*) FROM user_password")
        if (rs.next()) {
            println("user_password entries: ${rs.getInt(1)}")
        }
        DatabaseHelper.close(conn)
    }
}