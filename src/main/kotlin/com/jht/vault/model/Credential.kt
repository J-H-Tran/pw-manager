package com.jht.vault.model

import java.time.Instant

data class Credential(
    val id: Long,
    val accountId: Long,
    val username: String?,
    val label: String?,
    val passwordEnc: ByteArray,
    val iv: ByteArray,
    val updatedAt: Instant,
)