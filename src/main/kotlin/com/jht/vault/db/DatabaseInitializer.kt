package com.jht.vault.db

import com.jht.vault.util.CryptoUtils
import com.jht.vault.util.PasswordUtils
import java.sql.Connection
import java.sql.DriverManager

object DatabaseInitializer {
    val createTables = listOf(
        """
            CREATE TABLE IF NOT EXISTS master_password (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                encrypted_password BLOB NOT NULL,
                iv BLOB NOT NULL,
                aad BLOB,
                salt BLOB,
                algorithm TEXT DEFAULT 'AES-GCM'
            )
        """,
        """
            CREATE TABLE IF NOT EXISTS user_password (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                hash VARCHAR(128) NOT NULL
            )
        """
    )

    fun initialize(path: String) {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        val stmt = conn.createStatement()
        createTables.forEach { stmt.execute(it) }
        stmt.close()

        // Populate backup password if table is empty
        val rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM master_password")
        if (rs.next() && rs.getInt(1) == 0) {
            insertBackupPassword(conn)
        }
        rs.close()
        conn.close()
    }

    fun insertBackupPassword(conn: Connection) {
        conn.createStatement().execute("DELETE FROM master_password")
        val ps = conn.prepareStatement(
            "INSERT INTO master_password (encrypted_password, iv, aad, salt, algorithm) VALUES (?, ?, ?, ?, ?)"
        )
        val password = PasswordUtils.generateRandomPassword()
        val passwordByte = password.joinToString("").toByteArray(Charsets.UTF_8)
        println("Generated backup master password (store securely):")
        println(String(password))
        val salt = CryptoUtils.generateSalt()
        val key = CryptoUtils.deriveKey(password, salt)
        val aad = "backup".toByteArray(Charsets.UTF_8)
        val (encrypted, iv) = CryptoUtils.encryptAESGCM(passwordByte, key, aad)
        ps.setBytes(1, encrypted)
        ps.setBytes(2, iv)
        ps.setBytes(3, aad)
        ps.setBytes(4, salt)
        ps.setString(5, "AES-GCM")
        ps.executeUpdate()
        ps.close()
    }

    fun regenerateBackupPassword(path: String) {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        insertBackupPassword(conn)
        conn.close()
    }
}