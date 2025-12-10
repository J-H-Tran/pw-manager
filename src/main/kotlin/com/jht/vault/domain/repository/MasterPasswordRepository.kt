package com.jht.vault.domain.repository

import java.sql.Connection

class MasterPasswordRepository(
    private val conn: Connection
) {
    fun saveHash(hash: String) {
        val ps = conn.prepareStatement("INSERT INTO user_password (hash) VALUES (?)")
        ps.setString(1, hash)
        ps.executeUpdate()
    }

    fun getAllHashes(): List<String> {
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT hash FROM user_password")
        val hashes = mutableListOf<String>()
        while (rs.next()) {
            hashes.add(rs.getString(1))
        }
        return hashes
    }

    fun deleteAll() {
        val stmt = conn.createStatement()
        stmt.executeUpdate("DELETE FROM user_password")
    }
}