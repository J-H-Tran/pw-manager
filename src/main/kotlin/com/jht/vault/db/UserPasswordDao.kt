package com.jht.vault.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object UserPassword : Table() {
    val id = integer("id").autoIncrement()
    val hash = varchar("hash", 128)
    override val primaryKey = PrimaryKey(id)
}

class UserPasswordDao {
    fun insert(hash: String) = transaction {
        UserPassword.deleteAll()
        UserPassword.insert {
            it[UserPassword.hash] = hash
        }
    }

    fun get(): String? = transaction {
        UserPassword.selectAll().firstOrNull()?.get(UserPassword.hash)
    }

    fun exists(): Boolean = get() != null

    fun delete() = transaction {
        UserPassword.deleteAll()
    }
}