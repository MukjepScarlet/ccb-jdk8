package moe.lasoleil.toolkit.codec

import java.nio.charset.Charset
import java.util.*
import kotlin.math.min

object URLEncoder {

    private val dontNeedEncoding = BitSet(128)
    private val dontNeedEscaping = BitSet(128)

    init {
        dontNeedEncoding.set('a'.code, 'z'.code + 1)
        dontNeedEscaping.set('a'.code, 'z'.code + 1)
        dontNeedEncoding.set('A'.code, 'Z'.code + 1)
        dontNeedEscaping.set('A'.code, 'Z'.code + 1)
        dontNeedEncoding.set('0'.code, '9'.code + 1)
        dontNeedEscaping.set('0'.code, '9'.code + 1)

        dontNeedEncoding.set(' '.code)
        dontNeedEncoding.set('-'.code)
        dontNeedEncoding.set('_'.code)
        dontNeedEncoding.set('.'.code)
        dontNeedEncoding.set('*'.code)

        dontNeedEscaping.set('@'.code)
        dontNeedEscaping.set('*'.code)
        dontNeedEscaping.set('_'.code)
        dontNeedEscaping.set('+'.code)
        dontNeedEscaping.set('-'.code)
        dontNeedEscaping.set('.'.code)
        dontNeedEscaping.set('/'.code)
    }

    @JvmStatic
    @JvmOverloads
    fun encode(s: String, charset: Charset = Charsets.UTF_8): String {
        var needToChange = false
        val out = StringBuilder(s.length)
        val buffer = StringBuilder()

        var i = 0
        while (i < s.length) {
            var c = s[i].code
            if (dontNeedEncoding[c]) {
                if (c == ' '.code) {
                    c = '+'.code
                    needToChange = true
                }
                out.append(c.toChar())
                i++
            } else {
                do {
                    buffer.appendCodePoint(c)
                    if (c in 0xD800..0xDBFF) {
                        if ((i + 1) < s.length) {
                            val d = s[i + 1].code
                            if (d in 0xDC00..0xDFFF) {
                                buffer.appendCodePoint(d)
                                i++
                            }
                        }
                    }
                    i++
                } while (i < s.length && !dontNeedEncoding[s[i].code.also { c = it }])

                val str = buffer.toString()
                buffer.clear()
                val ba = str.toByteArray(charset)
                for (j in ba.indices) {
                    out.append('%')
                    val byte = ba[j].toInt()
                    out.append(hexDigitsUppercase[(byte shr 4) and 0xF])
                    out.append(hexDigitsUppercase[byte and 0xF])
                }
                needToChange = true
            }
        }

        return if (needToChange) out.toString() else s
    }

    @JvmStatic
    fun escape(input: String) = buildString(min(input.length, 10)) {
        for (c in input.toCharArray()) {
            val code = c.code
            when {
                dontNeedEscaping[code] -> appendCodePoint(code)

                code < 256 -> append('%')
                    .append(hexDigitsUppercase[(code shr 4) and 0xF])
                    .append(hexDigitsUppercase[code and 0xF])

                else -> append("%u")
                    .append(hexDigitsUppercase[(code shr 12) and 0xF])
                    .append(hexDigitsUppercase[(code shr 8) and 0xF])
                    .append(hexDigitsUppercase[(code shr 4) and 0xF])
                    .append(hexDigitsUppercase[code and 0xF])
            }
        }
    }

    @JvmStatic
    fun Iterable<Pair<String, String>>.toURLQueryParams() = buildString {
        for ((k, v) in this@toURLQueryParams) {
            if (this.isNotEmpty())
                append('&')

            append(k).append('=').append(v)
        }
    }

    @JvmSynthetic
    inline fun Collection<Pair<String, Any?>>.toURLQueryParams(
        sorted: Boolean = true,
        ignoreNull: Boolean = true,
        ignoreEmpty: Boolean = true,
        urlEncode: Boolean = false,
        predicate: (key: String, value: Any?) -> Boolean = { _, _ -> true }
    ) = buildString {
        val entries = this@toURLQueryParams.toTypedArray()
        if (sorted) {
            entries.sortBy { it.first }
        }

        for ((key, value) in entries) {
            if (ignoreNull && value == null)
                continue

            if (!predicate(key, value))
                continue

            var valueString = value.toString()
            if (ignoreEmpty && valueString.isEmpty())
                continue

            if (this.isNotEmpty())
                append('&')

            val keyString = if (urlEncode) encode(key) else key
            if (urlEncode) valueString = encode(valueString)

            append(keyString).append('=').append(valueString)
        }
    }
}