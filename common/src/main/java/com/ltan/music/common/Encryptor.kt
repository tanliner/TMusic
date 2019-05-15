package com.ltan.music.common

import android.org.apache.commons.codec.binary.Hex
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
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
    // private const val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    private const val RSA_TRANSFORMATION = "RSA/ECB/NoPadding"
    // private const val RSA_TRANSFORMATION = "RSA/None/PKCS1Padding"

    // Random character
    private const val BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    // const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB\n-----END PUBLIC KEY-----"
    const val PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB"

    fun test(seckey: String) {
        // val key = org.apache.commons.codec.binary.Hex.decodeHex(seckey.toCharArray())
        Hex.encodeHex(seckey.toByteArray())
        val md = MessageDigest.getInstance("hex")
    }
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

        // 得到公钥对象
        // val keySpec = X509EncodedKeySpec(strToByteArray(publicKey))
        val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.NO_PADDING))
        // val keySpec = X509EncodedKeySpec(getPublicKey().encoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey = keyFactory.generatePublic(keySpec)
        // 加密数据
        val cp = Cipher.getInstance(RSA_TRANSFORMATION)
        cp.init(Cipher.ENCRYPT_MODE, pubKey)

        val dstBuff = cp.doFinal(msgByte)

        return Base64.encodeToString(dstBuff, Base64.DEFAULT)
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
}