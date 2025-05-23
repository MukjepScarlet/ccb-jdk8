package moe.lasoleil.toolkit.codec

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

object MCipherDecoder {

    init {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private var encryptKey: String? = null

    fun setKey(publicKey: String) {
        encryptKey = publicKey.takeLast(30).substring(0, 8)
    }

    fun decode(urlString: String): String {
        requireNotNull(encryptKey) { "encryptKey is null, please set key first" }

        val tempString = URLDecoder.decode(urlString, Charsets.ISO_8859_1)
        val basedString = tempString.replace(',', '+')
        val tempBytes = Base64.decode(basedString)
        val tempSrcBytes = getSrcBytes(tempBytes, encryptKey!!.toByteArray(Charsets.ISO_8859_1))
        return String(tempSrcBytes, GBK)
    }

    private fun getSrcBytes(srcBytes: ByteArray, wrapKey: ByteArray): ByteArray {
        val key = SecretKeySpec(wrapKey, "DES")

        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding", "BC")

        cipher.init(Cipher.DECRYPT_MODE, key)

        val cipherText = cipher.doFinal(srcBytes)

        return cipherText
    }

}
