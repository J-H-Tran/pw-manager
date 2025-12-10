package com.jht.vault.repository

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

    fun isEmpty(): Boolean {
        val stmt = conn.prepareStatement("SELECT COUNT(*) FROM user_password")
        val rs = stmt.executeQuery()
        rs.next()
        val count = rs.getInt(1)
        rs.close()
        stmt.close()
        return count == 0
    }

    fun deleteAll() {
        val stmt = conn.createStatement()
        stmt.executeUpdate("DELETE FROM user_password")
    }
}