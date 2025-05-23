@file:JvmName("-Utils")

package moe.lasoleil.toolkit.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import moe.lasoleil.toolkit.codec.URLEncoder.toURLQueryParams
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import java.math.BigDecimal

internal val client = OkHttpClient.Builder()
    .build()

internal val objectMapper = jacksonObjectMapper()
    .registerModule(kotlinModule())

fun Map<String, String>.toFormBody() = FormBody.Builder().apply {
    forEach(::add)
}.build()

fun require(bigDecimal: BigDecimal, m: Int, n: Int) =
    require(bigDecimal.scale() <= n && (bigDecimal.precision() - bigDecimal.scale()) <= m)

internal fun Map<String, String>.orderedQueryParams(vararg keys: String) = keys.mapNotNull {
    val value = this[it].takeUnless(String?::isNullOrBlank) ?: return@mapNotNull null
    it to value
}.toURLQueryParams(sorted = false)

fun ResponseBody.json(): JsonNode = this.charStream().use(objectMapper::readTree)
