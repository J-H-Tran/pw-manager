package com.jht.vault.db

import com.jht.vault.util.CryptoUtils
import com.jht.vault.util.PasswordUtils
import java.security.SecureRandom
import java.sql.DriverManager

object DatabaseInitializer {
    val createTables = listOf(
        """
            CREATE TABLE IF NOT EXISTS header (
                id INTEGER PRIMARY KEY,
                wrapped_dek BLOB NOT NULL
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS account (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                notes TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS credential (
                id INTEGER PRIMARY KEY,
                account_id INTEGER NOT NULL,
                username TEXT,
                label TEXT,
                password_enc BLOB NOT NULL,
                iv BLOB NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY(account_id) REFERENCES account(id)
            );    """,
                """
            CREATE TABLE IF NOT EXISTS metadata (
                id INTEGER PRIMARY KEY,
                credential_id INTEGER NOT NULL,
                key TEXT NOT NULL,
                value TEXT,
                FOREIGN KEY(credential_id) REFERENCES credential(id)
            );
        """
    )
    private val createUserPasswordTable = """
        CREATE TABLE IF NOT EXISTS user_password (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            hash TEXT NOT NULL
        );
    """.trimIndent()

    fun initialize(path: String) {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        val stmt = conn.createStatement()
//        createTables.forEach { stmt.execute(it) }
        stmt.execute(createUserPasswordTable)
        stmt.close()

        // Populate backup passwords if table is empty
        val rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM user_password")
        if (rs.next() && rs.getInt(1) == 0) {
            insertBackupPasswords(conn)
        }
        rs.close()
        conn.close()
    }

    fun insertBackupPasswords(conn: java.sql.Connection) {
        conn.createStatement().execute("DELETE FROM user_password")
        val ps = conn.prepareStatement("INSERT INTO user_password (hash) VALUES (?)")
        repeat(4) {
            val password = PasswordUtils.generateRandomPassword()
            val hashed = CryptoUtils.hashPassword(password)
            ps.setString(1, hashed)
            ps.addBatch()
            password.fill('\u0000') // Clear password from memory
        }
        ps.executeBatch()
        ps.close()
    }

    fun regenerateBackupPasswords(path: String) {
        val url = "jdbc:sqlite:$path"
        val conn = DriverManager.getConnection(url)
        DatabaseInitializer.insertBackupPasswords(conn)
        conn.close()
    }
}