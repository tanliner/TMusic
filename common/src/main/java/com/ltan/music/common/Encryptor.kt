package com.ltan.music.common

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @describe :
 * @usage :
 *
 * </p>
 * Created by tanlin on 2019/5/15
 */
object Encryptor {
    const val TAG = "Encryptor"
    val KEY = strToByteArray("0CoJUm6Qyw8W8jud")
    private val ivx = strToByteArray("0102030405060708")
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    // private const val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding" // or padding 0 by yourself
    private const val RSA_TRANSFORMATION = "RSA/None/NoPadding"      // system inject 0 default
    // private const val RSA_TRANSFORMATION = "RSA/None/PKCS1Padding"

    // Random character
    const val BASE64 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\\/+"
    const val BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    const val BASE36 = "abcdefghijklmnopqrstuvwxyz0123456789"

    // const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB\n-----END PUBLIC KEY-----"
    const val PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB"

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class
    )
    @JvmStatic
    fun encrypt(message: String, seckey: ByteArray): String {
        val srcBuff = strToByteArray(message)
        return encrypt(message.toByteArray(), seckey)
    }

    @JvmStatic
    fun encrypt(msgByteArray: ByteArray, seckey: ByteArray): String {
        val skeySpec = SecretKeySpec(seckey, "AES")
        val ivSpec = IvParameterSpec(ivx)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(128)
        val key = keyGenerator.generateKey()

        // val IV = ByteArray(16)
        // val random = SecureRandom()
        // random.nextBytes(IV)

        val keySpec = SecretKeySpec(key.encoded, "AES")

        // val ivSpec2 = IvParameterSpec(IV)
        // cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec)
        // val byte1 = cipher.update(msgByteArray)
        // val byte2 = cipher.doFinal()
        //
        // val dstBuff = ByteBuffer.allocate(byte1.size + byte2.size)
        //     .put(byte1)
        //     .put(byte2)
        //     .array()
        val dstBuff = cipher.doFinal(msgByteArray)

        return Base64.encodeToString(dstBuff, Base64.NO_WRAP)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        UnsupportedEncodingException::class
    )
    @JvmStatic
    fun decrypt(encrypted: String, seckey: ByteArray): String {

        val skeySpec = SecretKeySpec(seckey, "AES")
        val ivSpec = IvParameterSpec(ivx)

        val ecipher = Cipher.getInstance(TRANSFORMATION)
        ecipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec)

        val raw = Base64.decode(encrypted, Base64.DEFAULT)

        val originalBytes = ecipher.doFinal(raw)

        return String(originalBytes, Charsets.UTF_8)
    }

    /**
     * RSA Encryption
     */
    @JvmStatic
    fun rsaEncrypt(message: String, publicKey: String): String {
        return rsaEncrypt(strToByteArray(message), publicKey)
    }

    fun rsaEncrypt(msgByte: ByteArray, publicKey: String): String {

        MusicLog.d(TAG, "msgByte length: ${msgByte.size}")
        var filledSecKey = msgByte
        val msgBytesLen = msgByte.size
        if (msgBytesLen < 128) {
            val buffer = ByteBuffer.allocate(128)
            buffer.position(128 - msgBytesLen)
            buffer.put(msgByte)
            filledSecKey = buffer.array()
        }


        // 得到公钥对象
        // val keySpec = X509EncodedKeySpec(strToByteArray(publicKey))
        // publicKey.toByteArray(Charsets.US_ASCII)
        val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.NO_PADDING))
        // val keySpec = X509EncodedKeySpec(getPublicKey().encoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey: RSAPublicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey

        // 加密数据
        val cp = Cipher.getInstance(RSA_TRANSFORMATION)
        cp.init(Cipher.ENCRYPT_MODE, pubKey)

        val dstBuff = cp.doFinal(filledSecKey)

        return byteArrayToHexString(dstBuff)
        // return Base64.encodeToString(dstBuff, Base64.NO_WRAP)
    }

    @JvmStatic
    fun rsaDecryption(privateKey: String, encrypted: String): String {
        // 得到私钥对象
        val keySpec = PKCS8EncodedKeySpec(strToByteArray(privateKey))
        val kf = KeyFactory.getInstance("RSA")
        val keyPrivate = kf.generatePrivate(keySpec)
        // 解密数据
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyPrivate)
        val originBytes = cipher.doFinal(strToByteArray(encrypted))

        return String(originBytes, Charsets.UTF_8)
    }

    @JvmStatic
    fun strToByteArray(origin: String, charset: Charset = Charset.defaultCharset()): ByteArray {
        return origin.toByteArray()
    }

    @JvmStatic
    fun randomBytes(len: Int): ByteArray {

        val ret = ByteArray(len)
        for (i in 1..len) {
            val r = (Math.random() * BASE62.length).toInt()
            ret[i - 1] = BASE62[r].toByte()
        }
        return ret
    }

    fun printByteArray(byteArray: ByteArray) {
        for (i in byteArray) {
            MusicLog.d(TAG, "find $i")
        }
        MusicLog.d(TAG, "printByteArray length: ${byteArray.size}")
    }

    fun byteArrayToHexString(bytes: ByteArray): String {
        val hexStr = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            hexStr.append(String.format("%02x", b))
        }
        return hexStr.toString()
    }

    fun generateNuid(): String {
        // CX448q%2F%2FDngy3mmIMWpmHVDyyo47tNN7tzGhOo9RNoRTc2PntSgA0%5CugKt9mhCiaxtjYPp%2B%5C31dRxiEryHZuIdqP5NcitCvNV5pU%2Fz53UAOlCHjereOORVvZDa1g7uSIa6nCHEjG72bIP%5CFOmAf70QqraHKBTEi%2B9CMBB3tO6jS%2BHJ%2Fd%3A1558026758515
        val md5 = MessageDigest.getInstance("MD5")
        val strBuilder = StringBuilder()
        val time = Date().toString()
        val name = "android-t-music"
        val addr = "chengdu"
        val ua = "android-chrome"
        val stringTxt = strBuilder.append(time)
            .append(name)
            .append(addr)
            .append(ua)
            .toString()
            .toByteArray()


        return md5.digest(stringTxt).toString()
    }

    @JvmStatic
    fun randomStr(pattern: String, len: Int): String {

        val sbuilder = StringBuilder()
        for (i in 0 until len ) {
            val idx = (Math.random() * pattern.length).toInt()
            val c = pattern[idx]
            sbuilder.append(c)
        }
        return sbuilder.toString()
    }
}