package moe.lasoleil.toolkit.ccb

import okhttp3.Request

fun interface CcbRequestBuilder {
    /**
     * 生成请求
     */
    fun build(publicKey: String): Request
}
