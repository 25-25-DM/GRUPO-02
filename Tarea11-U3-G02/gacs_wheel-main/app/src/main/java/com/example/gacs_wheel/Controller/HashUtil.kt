package com.example.gacs_wheel.Controller

import java.math.BigInteger
import java.security.MessageDigest

object HashUtil {
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
