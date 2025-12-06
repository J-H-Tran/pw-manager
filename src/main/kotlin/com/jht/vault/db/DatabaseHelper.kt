package com.jht.vault.db

import java.sql.Connection
import java.sql.DriverManager

object DatabaseHelper {
    fun open(path: String, passphrase: String): Connection {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        // For SQLCipher, you would execute: conn.createStatement().execute("PRAGMA key = '$passphrase';")
        return conn
    }
}