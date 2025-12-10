package com.jht.vault.domain.vo

data class EncryptedEntry(
    val encryptedPassword: ByteArray,
    val iv: ByteArray,
    val aad: ByteArray?,
    val salt: ByteArray,
    val algorithm: String
)