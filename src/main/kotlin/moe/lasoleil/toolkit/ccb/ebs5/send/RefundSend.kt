package moe.lasoleil.toolkit.ccb.ebs5.send

import moe.lasoleil.toolkit.util.require
import java.math.BigDecimal

class RefundSend : TransactionSend<RefundSend>("5W1004") {

    override val self get() = this

    init {
        requireBodyParams("MONEY", "ORDER")
    }

    /**
     * 退款金额
     */
    fun money(money: BigDecimal) = apply {
        require(money, 16, 2)
        addBody("MONEY", money.toPlainString())
    }

    /**
     * 原支付订单号
     */
    fun order(order: String) = apply {
        require(order.length in 1..30)
        addBody("ORDER", order)
    }

    /**
     * 退款流水号（可省略）
     * 注意：15位以内
     */
    fun refundCode(refundCode: String) = apply {
        require(refundCode.length in 1..15)
        addBody("REFUND_CODE", refundCode)
    }

}
