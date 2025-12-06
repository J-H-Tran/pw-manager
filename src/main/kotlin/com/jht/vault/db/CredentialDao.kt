package com.jht.vault.db

import com.jht.vault.model.Credential
import java.sql.Connection
import java.time.Instant

class CredentialDao(
    private val conn: Connection
) {
    fun updateCredential(id: Long, newEncrypted: ByteArray, newIv: ByteArray, updatedAt: Instant) {
        val sql = "UPDATE credentials SET password_enc = ?, iv = ?, updated_at = ? WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setBytes(1, newEncrypted)
        stmt.setBytes(2, newIv)
        stmt.setObject(3, updatedAt)
        stmt.setLong(4, id)
        stmt.executeUpdate()
        stmt.close()
    }

    fun deleteCredential(id: Long) {
        val sql = "DELETE FROM credentials WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setLong(1, id)
        stmt.executeUpdate()
        stmt.close()
    }

// In src/main/kotlin/com/jht/vault/db/CredentialDao.kt

    fun searchCredentials(query: String): List<Credential> {
        val sql = """
        SELECT id, account_id, username, label, password_enc, iv, updated_at
        FROM credentials
        WHERE username LIKE ? OR label LIKE ?
    """.trimIndent()
        val stmt = conn.prepareStatement(sql)
        val likeQuery = "%$query%"
        stmt.setString(1, likeQuery)
        stmt.setString(2, likeQuery)
        val rs = stmt.executeQuery()
        val result = mutableListOf<Credential>()
        while (rs.next()) {
            result.add(
                Credential(
                    id = rs.getLong("id"),
                    accountId = rs.getLong("account_id"),
                    username = rs.getString("username"),
                    label = rs.getString("label"),
                    passwordEnc = rs.getBytes("password_enc"),
                    iv = rs.getBytes("iv"),
                    updatedAt = rs.getObject("updated_at", Instant::class.java)
                )
            )
        }
        rs.close()
        stmt.close()
        return result
    }
}