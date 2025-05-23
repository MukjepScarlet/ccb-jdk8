package moe.lasoleil.toolkit.ccb.ebs5.send

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class AbstractQuerySend<T : AbstractQuerySend<T>>(txCode: String) : TransactionSend<T>(txCode) {

    // TODO 不可空字段判断
    init {
        requireBodyParams("KIND", "NORDERBY", "PAGE", "STATUS")
    }

    /**
     * 起始日期，yyyyMMdd
     */
    fun start(startDate: String): T {
        require(startDate.length in 1..8)
        addBody("START", startDate)
        return self
    }

    /**
     * 起始小时，HH
     */
    fun startHour(startHour: String): T {
        require(startHour.length in 1..2)
        addBody("STARTHOUR", startHour)
        return self
    }

    /**
     * 起始分钟，mm
     */
    fun startMinute(startMinute: String): T {
        require(startMinute.length in 1..2)
        addBody("STARTMIN", startMinute)
        return self
    }

    /**
     * 结束日期，yyyyMMdd
     */
    fun end(endDate: String): T {
        require(endDate.length in 1..8)
        addBody("END", endDate)
        return self
    }

    /**
     * 结束小时，HH
     */
    fun endHour(endHour: String): T {
        require(endHour.length in 1..2)
        addBody("ENDHOUR", endHour)
        return self
    }

    /**
     * 结束分钟，mm
     */
    fun endMinute(endMinute: String): T {
        require(endMinute.length in 1..2)
        addBody("ENDMIN", endMinute)
        return self
    }

    fun start(time: LocalDateTime): T {
        val formattedDate: String = time.format(dateFormatter)
        val formattedHour: String = time.format(hourFormatter)
        val formattedMinute: String = time.format(minuteFormatter)
        start(formattedDate)
        startHour(formattedHour)
        startMinute(formattedMinute)
        return self
    }

    fun end(time: LocalDateTime): T {
        val formattedDate: String = time.format(dateFormatter)
        val formattedHour: String = time.format(hourFormatter)
        val formattedMinute: String = time.format(minuteFormatter)
        end(formattedDate)
        endHour(formattedHour)
        endMinute(formattedMinute)
        return self
    }

    /**
     * 0:未结流水, 1:已结流水
     */
    fun kind(kind: String): T {
        require(kind.length in 1..1)
        addBody("KIND", kind)
        return self
    }

    /**
     * 查询指定订单
     * 若指定订单号则日期无效
     */
    fun order(order: String): T {
        require(order.length in 1..30)
        addBody("ORDER", order)
        return self
    }

    /**
     * 排序方法
     * 1:交易日期, 2:订单号
     */
    @JvmOverloads
    fun orderBy(order: String = "1"): T {
        require(order.length in 1..1)
        addBody("NORDERBY", order)
        return self
    }

    /**
     * 页码
     */
    fun page(page: Int): T {
        require(page >= 0) // TODO: 页面大小 page范围
        addBody("PAGE", page)
        return self
    }

    /**
     * 柜台号
     */
    fun posCode(posCode: String): T {
        require(posCode.length in 1..9)
        addBody("POSCODE", posCode)
        return self
    }

    /**
     * 订单状态
     * 0:交易失败, 1:交易成功, 2:待银行确认(针对未结流水查询), 3:全部
     */
    fun status(status: String): T {
        require(status.length in 1..1)
        addBody("STATUS", status)
        return self
    }

    /**
     * 子商户号
     */
    fun merchantNo(merchantNo: String): T {
        require(merchantNo.length in 1..32)
        addBody("Mrch_No", merchantNo)
        return self
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        private val hourFormatter = DateTimeFormatter.ofPattern("HH")
        private val minuteFormatter = DateTimeFormatter.ofPattern("mm")
    }

}
