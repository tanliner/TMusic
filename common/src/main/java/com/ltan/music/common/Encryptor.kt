package com.ltan.music.common

import android.annotation.SuppressLint
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: Encryptor
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
object Encryptor {
    const val TAG = "Encryptor"

    const val ENCRYPT_TYPE_HEX = "hex"
    const val ENCRYPT_TYPE_BASE64 = "base64"
    const val ENCRYPT_TYPE_DEF = ENCRYPT_TYPE_BASE64

    // for AES private key
    val KEY = "0CoJUm6Qyw8W8jud".toByteArray()
    val LINUX_API_KEY = "rFgB&h#%2?^eDg:Q".toByteArray()
    private val ivx = "0102030405060708".toByteArray()
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val TRANSFORMATION_ECB = "AES/ECB/PKCS5Padding" // equipment PKCS1
    // encrypt/algorithm/padding [ref-jianshu](https://juejin.im/post/5be103685188255e9b617dd7)
    // "RSA/ECB/PKCS1Padding" // or padding 0 by yourself
    private const val RSA_TRANSFORMATION = "RSA/None/NoPadding"      // system inject 0 default
    private const val RSA_MSG_MIN_LENGTH = 128

    // Random character
    const val BASE64 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\\/+"
    const val BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    const val BASE36 = "abcdefghijklmnopqrstuvwxyz0123456789"

    // const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nTHE_PUBLIC_KEY_CONTENT\n-----END PUBLIC KEY-----"
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
        return encrypt(message.toByteArray(), seckey)
    }

    @JvmStatic
    fun encrypt(msgByteArray: ByteArray, seckey: ByteArray): String {
        val skeySpec = SecretKeySpec(seckey, "AES")
        val ivSpec = IvParameterSpec(ivx)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec)
        val dstBuff = cipher.doFinal(msgByteArray)

        return Base64.encodeToString(dstBuff, Base64.NO_WRAP)
    }

    @JvmStatic
    fun encryptLinuxApi(msg: String, seckey: ByteArray): String {
        return encryptLinuxApi(msg.toByteArray(), seckey)
    }
    @SuppressLint("GetInstance")
    @JvmStatic
    fun encryptLinuxApi(msgByteArray: ByteArray, secKey: ByteArray): String {
        val skeySpec = SecretKeySpec(secKey, "AES")
        val cipher = Cipher.getInstance(TRANSFORMATION_ECB)

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
        val dstBuff = cipher.doFinal(msgByteArray)

        return byteArrayToHexString(dstBuff, true)
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

    fun rsaEncrypt(msgByte: ByteArray, publicKey: String, type: String = ENCRYPT_TYPE_DEF): String {
        var filledSecKey = msgByte

        // the `RSA/None/NoPadding` will insert 0 automatically
        // val msgBytesLen = msgByte.size
        // if (msgBytesLen < RSA_MSG_MIN_LENGTH) {
        //     val buffer = ByteBuffer.allocate(RSA_MSG_MIN_LENGTH)
        //     buffer.position(RSA_MSG_MIN_LENGTH - msgBytesLen)
        //     buffer.put(msgByte)
        //     filledSecKey = buffer.array()
        // }

        // to get the public key
        // val keySpec = X509EncodedKeySpec(strToByteArray(publicKey))
        val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.NO_PADDING))
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey: RSAPublicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey

        // encryption core
        val cp = Cipher.getInstance(RSA_TRANSFORMATION)
        cp.init(Cipher.ENCRYPT_MODE, pubKey)

        val dstBuff = cp.doFinal(filledSecKey)

        return when (type) {
            ENCRYPT_TYPE_BASE64 -> Base64.encodeToString(dstBuff, Base64.DEFAULT)
            ENCRYPT_TYPE_HEX -> byteArrayToHexString(dstBuff)
            else -> ""
        }
    }

    @JvmStatic
    fun rsaDecryption(privateKey: String, encrypted: String): String {
        // to get the public key
        val keySpec = PKCS8EncodedKeySpec(strToByteArray(privateKey))
        val kf = KeyFactory.getInstance("RSA")
        val keyPrivate = kf.generatePrivate(keySpec)
        // decrypt txt
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

    @JvmStatic
    fun byteArrayToHexString(bytes: ByteArray, upperCase: Boolean = false): String {
        val hexStr = StringBuilder(bytes.size * 2)
        val pattern = if (upperCase) "%02X" else "%02x"
        for (b in bytes) {
            hexStr.append(String.format(pattern, b))
        }
        return hexStr.toString()
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