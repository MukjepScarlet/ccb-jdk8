package moe.lasoleil.toolkit.ccb.merchantqr

import moe.lasoleil.toolkit.ccb.CcbRequestBuilder
import moe.lasoleil.toolkit.codec.md5Hex
import moe.lasoleil.toolkit.util.orderedQueryParams
import moe.lasoleil.toolkit.util.toFormBody
import okhttp3.Request

class CloseMerchantQrOrderParams : CcbRequestBuilder {

    private val params = HashMap<String, String>().apply {
        put("TXCODE", "530550")
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
     * 要关闭的订单号
     */
    fun orderId(orderId: String) = apply {
        require(orderId.length in 1..30)
        params["ORDERID"] = orderId
    }

    /**
     * @param txCode 530550 (only this)
     */
    @JvmOverloads
    fun txCode(txCode: String = "530550") = apply {
        require(txCode.length in 1..6)
        params["TXCODE"] = txCode
    }

    override fun build(publicKey: String): Request {
        val copyOfParams = HashMap(params)
        copyOfParams["PUB"] = publicKey.takeLast(30)

        params["MAC"] = copyOfParams.orderedQueryParams(
            "MERCHANTID", "POSID", "BRANCHID", "ORDERID", "TXCODE", "SUB_APPID", "PUB"
        ).md5Hex()

        return Request.Builder().url(URL).post(params.toFormBody()).build()
    }

    companion object {
        private const val URL = "https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_00?CCB_IBSVersion=V6"
    }

}
