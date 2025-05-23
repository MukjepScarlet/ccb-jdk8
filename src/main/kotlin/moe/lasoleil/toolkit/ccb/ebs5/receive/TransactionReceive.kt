package moe.lasoleil.toolkit.ccb.ebs5.receive

import com.fasterxml.jackson.module.kotlin.readValue
import moe.lasoleil.toolkit.codec.GB18030
import moe.lasoleil.toolkit.codec.xmlMapper
import okhttp3.Response

class TransactionReceive internal constructor(
    full: Map<String, Any> // XML body
) {

    @Suppress("UNCHECKED_CAST")
    private val params: Map<String, Any> = try {
        full["TX"] as Map<String, Any>
    } catch (e: ClassCastException) {
        throw IllegalArgumentException("Invalid response, real type is ${full.javaClass.simpleName}, value is $full", e)
    }

    @get:JvmName("requestSn")
    val requestSn: String get() = params["REQUEST_SN"] as String

    @get:JvmName("txCode")
    val txCode: String get() = params["TX_CODE"] as String

    @get:JvmName("custId")
    val custId: String get() = params["CUST_ID"] as String

    @get:JvmName("returnCode")
    val returnCode: String get() = params["RETURN_CODE"] as String

    @get:JvmName("returnMsg")
    val returnMsg: String get() = params["RETURN_MSG"] as String

    @get:JvmName("language")
    val language: String get() = params["LANGUAGE"] as String

    @get:JvmName("info")
    @Suppress("UNCHECKED_CAST")
    val info: Map<String, Any> get() = params["TX_INFO"] as Map<String, Any>

    companion object {
        @JvmStatic
        @JvmName("from")
        fun Response.transactionBody(): TransactionReceive = use {
            val reader = this.body.byteStream().reader(GB18030)
            TransactionReceive(xmlMapper.readValue<Map<String, Any>>(reader))
        }
    }

}
