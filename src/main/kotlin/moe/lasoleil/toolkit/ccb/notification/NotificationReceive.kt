package moe.lasoleil.toolkit.ccb.notification

import moe.lasoleil.toolkit.codec.MCipherDecoder
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NotificationReceive(
    private val full: Map<String, String> // TODO: 到底是JSON还是FORM ?
) {

    private operator fun get(key: String): String? {
        return full[key]
    }

    fun posId(): String = get("POSID")!!

    fun branchId(): String = get("BRANCHID")!!

    fun orderId(): String = get("ORDERID")!!

    fun paymentString(): String = get("PAYMENT")!!

    fun payment(): BigDecimal = paymentString().toBigDecimal()

    fun curCode(): String = get("CURCODE")!!

    fun remark1(): String? = get("REMARK1")

    fun remark2(): String? = get("REMARK2")

    fun accountType(): String? = get("ACC_TYPE")

    fun successString(): String = get("SUCCESS")!!

    fun success(): Boolean {
        val flag = successString()
        require(flag == "Y" || flag == "N") { "Illegal SUCCESS flag: $flag" }
        return flag == "Y"
    }

    fun type(): String? = get("TYPE")

    fun referer(): String? = get("REFERER")

    fun clientIp(): String? = get("CLIENTIP")

    fun accDateString(): String? = get("ACCDATE")

    fun accDate(): LocalDate? = accDateString()?.let { LocalDate.parse(it, dateFormatter) }

    fun userMsg(): String? = get("USRMSG")

    fun decodedUserMsg(): Pair<String, String>? = userMsg()?.let {
        val (first, second) = MCipherDecoder.decode(it).split("|")
        first to second
    }

    fun installNum(): String? = get("INSTALLNUM")

    fun errMsg(): String? = get("ERRMSG")

    fun userInfo(): String? = get("USRINFO")

    fun sign(): String = get("SIGN")!!

    private fun src(): String = buildList(20) {
        fun String?.value() = this ?: ""

        add("POSID=${posId().value()}")
        add("BRANCHID=${branchId().value()}")
        add("ORDERID=${orderId().value()}")
        add("PAYMENT=${paymentString().value()}")
        add("CURCODE=${curCode().value()}")
        add("REMARK1=${remark1().value()}")
        add("REMARK2=${remark2().value()}")
        add("ACC_TYPE=${accountType().value()}")
        add("SUCCESS=${successString().value()}")
        add("TYPE=${type().value()}")
        add("REFERER=${referer().value()}")
        add("CLIENTIP=${clientIp().value()}")
        add("ACCDATE=${accDateString().value()}")
        add("USRMSG=${userMsg().value()}")
        add("INSTALLNUM=${installNum().value()}")
        userInfo()?.let { add("USRINFO=$it") }
    }.joinToString("&")

    fun checkSign(publicKey: String): Boolean {
        val rsaSig = CCBSign.RSASig()
        rsaSig.publicKey = publicKey.takeLast(30)
        return rsaSig.verifySigature(sign(), src())
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

}