package com.jht.vault.db

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

object DatabaseHelper {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun open(path: String, passphrase: String): Connection {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        // For SQLCipher, you would execute: conn.createStatement().execute("PRAGMA key = '$passphrase';")
        return conn
    }

    fun close(conn: Connection?) {
        try {
            conn?.close()
            logger.info("Closed database connection.")
        } catch (e: Exception) {
            logger.error("Error closing database connection.", e)
        }
    }
}