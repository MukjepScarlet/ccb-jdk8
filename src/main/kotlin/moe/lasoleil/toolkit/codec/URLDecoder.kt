package moe.lasoleil.toolkit.codec

import java.nio.charset.Charset

object URLDecoder {

    @JvmStatic
    @JvmOverloads
    fun decode(s: String, charset: Charset = Charsets.UTF_8): String {
        var needToChange = false
        val numChars = s.length
        val sb = StringBuilder(if (numChars > 500) numChars / 2 else numChars)
        var i = 0

        var c: Char
        var bytes: ByteArray? = null
        while (i < numChars) {
            c = s[i]
            when (c) {
                '+' -> {
                    sb.append(' ')
                    i++
                    needToChange = true
                }

                '%' -> {
                    try {
                        if (bytes == null) bytes = ByteArray((numChars - i) / 3)
                        var pos = 0

                        while (((i + 2) < numChars) &&
                            (c == '%')
                        ) {
                            val v = s.substring(i + 1, i + 3).toInt(16)
                            if (v < 0) throw IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value")
                            bytes[pos++] = v.toByte()
                            i += 3
                            if (i < numChars) c = s[i]
                        }

                        // A trailing, incomplete byte encoding such as
                        // "%x" will cause an exception to be thrown
                        if ((i < numChars) && (c == '%')) throw IllegalArgumentException(
                            "URLDecoder: Incomplete trailing escape (%) pattern"
                        )

                        sb.append(String(bytes, 0, pos, charset))
                    } catch (e: NumberFormatException) {
                        throw IllegalArgumentException(
                            "URLDecoder: Illegal hex characters in escape (%) pattern - "
                                    + e.message
                        )
                    }
                    needToChange = true
                }

                else -> {
                    sb.append(c)
                    i++
                }
            }
        }

        return (if (needToChange) sb.toString() else s)
    }
}
