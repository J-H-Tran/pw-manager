package com.jht.vault.domain.repository

import java.sql.Connection

class UserPasswordRepository(
    private val conn: Connection
) {
    fun saveHash(hash: String) {
        val ps = conn.prepareStatement("INSERT INTO user_password (hash) VALUES (?)")
        ps.setString(1, hash)
        ps.executeUpdate()
    }

    fun getHash(): String? {
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT hash FROM user_password")
        return if (rs.next()) rs.getString(1) else null
    }

    fun deleteAll() {
        val stmt = conn.createStatement()
        stmt.executeUpdate("DELETE FROM user_password")
    }
}