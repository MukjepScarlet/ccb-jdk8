@file:JvmName("Codec")

package moe.lasoleil.toolkit.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import okhttp3.MediaType.Companion.toMediaType
import okio.ByteString.Companion.encodeUtf8
import java.nio.charset.Charset

@JvmField
val GBK: Charset = Charset.forName("GBK")

@JvmField
val GB2312: Charset = Charset.forName("GB2312")

@JvmField
val GB18030: Charset = Charset.forName("GB18030")

val xmlMapper: ObjectMapper = XmlMapper()
    .registerModule(kotlinModule())

internal val MEDIA_TYPE = "application/x-www-form-urlencoded; charset=GB18030".toMediaType()

internal val hexDigitsUppercase =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
internal val hexDigitsLowercase =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

fun String.md5Hex() = this.encodeUtf8().md5().hex()
