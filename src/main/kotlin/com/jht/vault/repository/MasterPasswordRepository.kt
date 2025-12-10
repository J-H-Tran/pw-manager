package com.jht.vault.repository

import com.jht.vault.domain.vo.EncryptedEntry
import java.sql.Connection

class MasterPasswordRepository(
    private val conn: Connection
) {
    fun saveMasterPassword(
        encryptedPassword: ByteArray,
        iv: ByteArray,
        aad: ByteArray?,
        salt: ByteArray,
        algorithm: String = "AES-GCM"
    ) {
        val ps = conn.prepareStatement(
            "INSERT INTO master_password (encrypted_password, iv, aad, salt, algorithm) VALUES (?, ?, ?, ?, ?)"
        )
        ps.setBytes(1, encryptedPassword)
        ps.setBytes(2, iv)
        ps.setBytes(3, aad)
        ps.setBytes(4, salt)
        ps.setString(5, algorithm)
        ps.executeUpdate()
        ps.close()
    }

    fun getMasterPassword(): EncryptedEntry? {
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT encrypted_password, iv, aad, salt, algorithm FROM master_password ORDER BY id DESC LIMIT 1")
        val entry = if (rs.next()) {
            EncryptedEntry(
                encryptedPassword = rs.getBytes("encrypted_password"),
                iv = rs.getBytes("iv"),
                aad = rs.getBytes("aad"),
                salt = rs.getBytes("salt"),
                algorithm = rs.getString("algorithm")
            )
        } else null
        rs.close()
        stmt.close()
        return entry
    }

    fun deleteAll() {
        val stmt = conn.createStatement()
        stmt.executeUpdate("DELETE FROM master_password")
        stmt.close()
    }
}