package com.jht.vault.db

import com.jht.vault.model.Account
import java.sql.Connection
import java.time.Instant

class AccountDao(
    private val conn: Connection
) {
    fun listAccounts(): List<Account> {
        val stmt = conn.prepareStatement("SELECT id, name, notes FROM accounts")
        val rs = stmt.executeQuery()
        val result = mutableListOf<Account>()
        while (rs.next()) {
            result.add(Account(rs.getLong("id"), rs.getString("name"), rs.getString("notes")))
        }
        rs.close()
        stmt.close()
        return result
    }

    fun searchAccounts(query: String): List<Account> {
        val stmt = conn.prepareStatement(
            "SELECT id, name, notes FROM accounts WHERE name LIKE ? OR notes LIKE ?"
        )
        val likeQuery = "%$query%"
        stmt.setString(1, likeQuery)
        stmt.setString(2, likeQuery)
        val rs = stmt.executeQuery()
        val result = mutableListOf<Account>()
        while (rs.next()) {
            result.add(Account(rs.getLong("id"), rs.getString("name"), rs.getString("notes")))
        }
        rs.close()
        stmt.close()
        return result
    }
}