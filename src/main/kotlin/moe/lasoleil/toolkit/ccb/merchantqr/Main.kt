package moe.lasoleil.toolkit.ccb.merchantqr

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import moe.lasoleil.toolkit.codec.URLEncoder
import moe.lasoleil.toolkit.util.client
import moe.lasoleil.toolkit.util.json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.math.BigDecimal

/**
 * 商户号：105000075239903
 * 分行号：351000000
 * 柜台号1:087555889（公众号缴费）
 * 柜台号2：087555920（线下缴费）
 */

fun main(vararg args: String) {
    val publicKey =
        "30819c300d06092a864886f70d010101050003818a003081860281806ff83c56852ba9ed95322d5e1d99359c9e1c36782fde9e03e163063a7832b57007eac089d43c25db91c4e014957901bf98603b1c9efc9ded873e196d7649cc35cb6bf523fbe81ba183ae16b6d3d330db5f0c91329b7a4a7ea833016b58cb0f20d669f55c805fd076cd5640e5affeda9dc7f30c8af6528d5c335b7092fc9ec1b3020111"

    val bankURL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain"
//    bankURL = "http://124.127.94.61:8001/CCBIS/ccbMain"

    val MERCHANTID = "105421097080009"
    val POSID = "902807340"
    val BRANCHID = "360000000"
    val ORDERID = "111111111111"
    val PAYMENT = "0.01"
    val CURCODE = "01"
    val TXCODE = "530550"
//    val REMARK1 = ""
//    val REMARK2 = ""
    val RETURNTYPE = "4"
    val TIMEOUT = ""
    val PUB32TR2 = "f6528d5c335b7092fc9ec1b3020111"

    val request = MerchantQrParams()
        .merchantId(MERCHANTID)
        .posId(POSID)
        .branchId(BRANCHID)
        .orderId(ORDERID)
        .payment(BigDecimal(PAYMENT))
        .curCode(CURCODE)
        .txCode(TXCODE)
        .returnType(RETURNTYPE)
//        .timeout(TIMEOUT)
        .build(PUB32TR2)

    URLEncoder.encode("ddd")

    val ret = client.newCall(request).execute().body.json()

    if ((ret as ObjectNode).get("SUCCESS").asBoolean()) {
        val payUrl = ret.get("PAYURL").asText()
        println(ret)
    } else {
        println("ERROR!")
        println("ret::$ret")
    }

}