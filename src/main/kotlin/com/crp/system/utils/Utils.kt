package com.crp.system.utils

import com.crp.system.utils.extensions.DateFormatType
import com.crp.system.utils.extensions.toStringFormat
import com.google.gson.Gson
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


object Utils {

    fun getRandomSixNumberString(): String {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        val rnd = Random()
        val number = rnd.nextInt(999999)

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number)
    }

    fun getRandomSixNumber(): Int {
        return  getRandomSixNumberString().toInt()
    }

    fun currentTime(type: DateFormatType = DateFormatType.longDateAndLongTime): String? {
        val time = Calendar.getInstance().time
        return time.toStringFormat(type)
    }

    fun md5(value: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(value.toByteArray(Charsets.UTF_8)))
        return String.format("%032x", bigInt)
    }

    @Throws(Exception::class)
    fun encrypt(message: String, publicKey: Key): String? {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.toByteArray(StandardCharsets.UTF_8)))
    }

    @Throws(Exception::class)
    fun decrypt(encryptedMessage: String?, privateKey: Key): String? {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)), StandardCharsets.UTF_8)
    }
}

fun String.addUrlParam(key: String, value: Any): String {
    if (this.contains("?")) {
        return "$this&$key=$value"
    }
    return "$this?$key=$value"
}

fun String.toBase64(): String? {
    return Base64.getEncoder().encodeToString(this.toByteArray())
}

fun String.fromBase64(): String? {
    return String(Base64.getDecoder().decode(this))
}

fun getPublicKey(): PublicKey {
    val resourceLoader = DefaultResourceLoader()
    val resource = resourceLoader.getResource("classpath:public.key")
    val base64Key = resource.asString()
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(base64Key))
    val pubKey = kf.generatePublic(keySpecX509)
    return pubKey
}

fun getPrivateKey(): PrivateKey {
    val resourceLoader = DefaultResourceLoader()
    val resource = resourceLoader.getResource("classpath:private.key")
    val base64Key =  resource.asString()
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64Key))
    val privKey: PrivateKey = kf.generatePrivate(keySpecPKCS8)
    return privKey
}

@Throws
private fun Resource.asString(): String {
    try {
        InputStreamReader(this.inputStream).use { reader ->
            return FileCopyUtils.copyToString(reader).replace("\n", "")
        }
    } catch (e: IOException) {
        throw UncheckedIOException(e)
    }
}

val gson = Gson()
inline fun <reified A> A.toJson(): String {
    return gson.toJson(this).toString()
}

inline fun <reified A> A?.isNull(): Boolean {
    return this == null
}