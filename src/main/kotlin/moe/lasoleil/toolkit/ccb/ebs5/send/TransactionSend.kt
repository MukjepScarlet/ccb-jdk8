package moe.lasoleil.toolkit.ccb.ebs5.send

import moe.lasoleil.toolkit.codec.GB18030
import moe.lasoleil.toolkit.codec.MEDIA_TYPE
import moe.lasoleil.toolkit.codec.xmlMapper
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.ByteString.Companion.encode

abstract class TransactionSend<T : TransactionSend<T>>(txCode: String) {
    
    abstract val self: T

    private val requiredParams = hashSetOf<String>()
    private val bodyRequiredParams = hashSetOf<String>()

    private val params = HashMap<String, Any>()

    init {
        requiredParams += "REQUEST_SN"
        requiredParams += "CUST_ID"
        requiredParams += "USER_ID"
        requiredParams += "PASSWORD"
        requiredParams += "TX_CODE"
        requiredParams += "LANGUAGE"
        this.txCode(txCode)
        language()
    }

    protected fun requireParams(vararg keys: String) {
        requiredParams.addAll(keys)
    }

    protected fun requireBodyParams(vararg keys: String) {
        bodyRequiredParams.addAll(keys)
    }

    fun requestSn(requestSn: String): T {
        require(requestSn.length in 1..16)
        params["REQUEST_SN"] = requestSn
        return self
    }

    fun custId(custId: String): T {
        require(custId.length in 1..21)
        params["CUST_ID"] = custId
        return self
    }

    fun userId(userId: String): T {
        require(userId.length in 1..21)
        params["USER_ID"] = userId
        return self
    }

    fun password(password: String): T {
        require(password.length in 1..32)
        params["PASSWORD"] = password
        return self
    }

    private fun txCode(txCode: String): T {
        require(txCode.length in 1..6)
        params["TX_CODE"] = txCode
        return self
    }

    @JvmOverloads
    fun language(language: String = "CN"): T {
        require(language.length in 1..2)
        params["LANGUAGE"] = language
        return self
    }

    fun addBody(key: String, value: Any): T {
        @Suppress("UNCHECKED_CAST")
        val body = params.getOrPut("TX_INFO") { LinkedHashMap<String, Any>() } as LinkedHashMap<String, Any>
        body[key] = value
        return self
    }

    fun toXmlText(): String {
        // Verify required parameters
        requiredParams.forEach {
            require(params.containsKey(it)) { "Missing required parameter: $it" }
        }

        // Verify body required parameters
        if (bodyRequiredParams.isNotEmpty()) {
            require(params.containsKey("TX_INFO")) { "Missing required parameter: TX_INFO" }
            @Suppress("UNCHECKED_CAST")
            val body = params["TX_INFO"] as LinkedHashMap<String, Any>
            bodyRequiredParams.forEach {
                require(body.containsKey(it)) { "Missing required parameter: $it" }
            }
        }

        return Buffer().use {
            it.writeUtf8(XML_HEADER)
            xmlMapper.writer()
                .withRootName("TX")
                .writeValue(it.outputStream(), params)
            it.readUtf8()
        }
    }

    fun toRequestBody(): RequestBody {
        // Convert to XML bytes
        val bytes = ("requestXml=" + toXmlText()).encode(GB18030)
        return bytes.toRequestBody(MEDIA_TYPE)
    }

    companion object {
        const val XML_HEADER = """<?xml version="1.0" encoding="GB18030" standalone="yes" ?>\n"""
    }

}
