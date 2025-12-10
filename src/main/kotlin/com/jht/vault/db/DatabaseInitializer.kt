package com.jht.vault.db

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
        conn.close()
    }
}