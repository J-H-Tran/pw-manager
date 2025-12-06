package com.jht.vault.util

object MetadataUtils {
    fun encryptMetadata(value: String, key: ByteArray, aad: ByteArray): Pair<ByteArray, ByteArray> {
        // Use your CryptoUtils.encryptAESGCM
        return CryptoUtils.encryptAESGCM(value.toByteArray(Charsets.UTF_8), key, aad)
    }

    fun decryptMetadata(iv: ByteArray, encrypted: ByteArray, key: ByteArray, aad: ByteArray): String {
        // Use your CryptoUtils.decryptAESGCM (implement if missing)
        return String(CryptoUtils.decryptAESGCM(iv, encrypted, key, aad), Charsets.UTF_8)
    }
}