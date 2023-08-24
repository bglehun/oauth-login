package com.test.api.common.util

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object EncryptUtil {
    fun encrypt(algorithm: String, secretKey: String, message: String): String {
        val key = SecretKeySpec(secretKey.toByteArray(), algorithm)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encryptedPhoneNumber = cipher.doFinal(message.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedPhoneNumber)
    }

    fun decrypt(algorithm: String, secretKey: String, digest: String): String {
        val key = SecretKeySpec(secretKey.toByteArray(), algorithm)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key)

        return String(cipher.doFinal(Base64.getDecoder().decode(digest)))
    }
}
