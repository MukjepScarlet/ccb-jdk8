package moe.lasoleil.toolkit.ccb.merchantqr

import moe.lasoleil.toolkit.ccb.CcbRequestBuilder
import moe.lasoleil.toolkit.codec.URLEncoder
import moe.lasoleil.toolkit.codec.md5Hex
import moe.lasoleil.toolkit.util.orderedQueryParams
import moe.lasoleil.toolkit.util.require
import moe.lasoleil.toolkit.util.toFormBody
import okhttp3.Request
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MerchantQrParams : CcbRequestBuilder {

    private val params = HashMap<String, String>().apply {
        put("CURCODE", "01")
        put("TXCODE", "530550")
        put("RETURNTYPE", "4")
    }

    /**
     * 商户号
     */
    fun merchantId(merchantId: String) = apply {
        require(merchantId.length in 1..15)
        params["MERCHANTID"] = merchantId
    }

    /**
     * 柜台号
     */
    fun posId(posId: String) = apply {
        require(posId.length in 1..9)
        params["POSID"] = posId
    }

    /**
     * 分行号
     */
    fun branchId(branchId: String) = apply {
        require(branchId.length in 1..9)
        params["BRANCHID"] = branchId
    }

    /**
     * 订单号（自定义）
     * 建议使用商户号后9位+21位自定义（数字/字母/下划线）
     */
    fun orderId(orderId: String) = apply {
        require(orderId.length in 1..30)
        params["ORDERID"] = orderId
    }

    /**
     * 金额，单位元
     * 例如2元1分表示为2.01
     */
    fun payment(payment: BigDecimal) = apply {
        require(payment, 16, 2)
        params["PAYMENT"] = payment.toPlainString()
    }

    /**
     * @param curCode 01-RMB (only this)
     */
    @JvmOverloads
    fun curCode(curCode: String = "01") = apply {
        require(curCode.length in 1..2)
        params["CURCODE"] = curCode
    }

    /**
     * @param txCode 530550 (only this)
     */
    @JvmOverloads
    fun txCode(txCode: String = "530550") = apply {
        require(txCode.length in 1..6)
        params["TXCODE"] = txCode
    }

    /**
     * 返回类型
     */
    @JvmOverloads
    fun returnType(returnType: String = "4") = apply {
        require(returnType.length in 1..6)
        params["RETURNTYPE"] = returnType
    }

    /**
     * 订单超时时间，超过此时间订单作废
     */
    fun timeout(timeout: String) = apply {
        require(timeout.length == 14) { "timeout should matches: yyyyMMddHHmmss" }
        val parsed = runCatching { LocalDateTime.parse(timeout, timeoutFormatter) }.getOrNull()
        requireNotNull(parsed) { "timeout should matches: yyyyMMddHHmmss" }
        params["TIMEOUT"] = timeout
    }

    /**
     * 订单超时时间，超过此时间订单作废
     */
    fun timeout(timeout: LocalDateTime) = apply {
        params["TIMEOUT"] = timeout.format(timeoutFormatter)
    }

    /**
     * 商品信息（备注）
     */
    fun proInfo(proInfo: String) = apply {
        val encoded = URLEncoder.escape(proInfo)
        require(encoded.length in 1..128)
        params["PROINFO"] = encoded
    }

    /**
     * 退出支付时返回地址
     */
    fun merchantUrl(merchantUrl: String) = apply {
        require(merchantUrl.length in 1..1800)
        params["Mrch_url"] = merchantUrl
    }

    override fun build(publicKey: String): Request {
        val copyOfParams = HashMap(params)
        copyOfParams["PUB"] = publicKey.takeLast(30)

        params["MAC"] = copyOfParams.orderedQueryParams(
            "MERCHANTID", "POSID", "BRANCHID", "ORDERID", "PAYMENT", "CURCODE", "TXCODE",
            "REMARK1", "REMARK2", "RETURNTYPE", "TIMEOUT", "PUB"
        ).md5Hex()

        return Request.Builder().url(URL).post(params.toFormBody()).build()
    }

    companion object {
        @JvmStatic
        private val timeoutFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

        private const val URL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?CCB_IBSVersion=V6"
    }

}
