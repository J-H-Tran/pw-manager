package com.jht.vault

import com.jht.vault.db.UserPassword
import com.jht.vault.db.UserPasswordDao
import de.mkammerer.argon2.Argon2Factory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object BackupPasswords : Table() {
    val id = integer("id").autoIncrement()
    val hash = varchar("hash", 128)
    override val primaryKey = PrimaryKey(id)
}

object PasswordManager {
    private val argon2 = Argon2Factory.create()
    private const val BACKUP_COUNT = 4
    private val userPasswordDao = UserPasswordDao()

    init {
        Database.connect("jdbc:sqlite:passwords.db", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(BackupPasswords, UserPassword)
        }
        if (getBackupHashes().isEmpty()) {
            refreshBackupPasswords()
        }
    }

    fun hashPassword(password: CharArray): String {
        val hash = argon2.hash(2, 65536, 1, password)
        password.fill('\u0000')
        return hash
    }

    fun verifyPassword(hash: String, password: CharArray): Boolean {
        val result = argon2.verify(hash, password)
        password.fill('\u0000')
        return result
    }

    fun setUserPassword(newPassword: CharArray) {
        val hash = hashPassword(newPassword)
        userPasswordDao.insert(hash)
    }

    fun refreshBackupPasswords() {
        transaction {
            BackupPasswords.deleteAll()
            repeat(BACKUP_COUNT) {
                val pwd = UUID.randomUUID().toString().substring(0, 8).toCharArray()
                val hash = hashPassword(pwd)
                pwd.fill('\u0000')
                BackupPasswords.insert { it[BackupPasswords.hash] = hash }
            }
        }
    }

    fun authenticate(input: CharArray): Boolean {
        val userHash = userPasswordDao.get()
        if (userHash != null && verifyPassword(userHash, input)) {
            return true
        }
        val backupHashes = getBackupHashes()
        if (backupHashes.any { verifyPassword(it, input) }) {
            refreshBackupPasswords()
            return true
        }
        return false
    }

    private fun getBackupHashes(): List<String> = transaction {
        BackupPasswords.selectAll().map { it[BackupPasswords.hash] }
    }

    fun hasUserPassword(): Boolean = userPasswordDao.exists()
}