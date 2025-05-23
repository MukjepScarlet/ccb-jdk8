@file:JvmName("XmlRequests")
package moe.lasoleil.toolkit.ccb.ebs5

import moe.lasoleil.toolkit.ccb.ebs5.send.TransactionSend
import moe.lasoleil.toolkit.codec.GB18030
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.ByteString.Companion.encode
import okio.buffer
import okio.sink
import okio.source
import java.net.Socket

class SocketRequest(
    address: String,
    port: Int,
) : AutoCloseable {

    private val socket = Socket(address, port)

    private val source = socket.getInputStream().source().buffer()
    private val sink = socket.getOutputStream().sink().buffer()

    @Synchronized
    fun send(content: String): String {
        sink.write(content.encode(GB18030))
        sink.flush()
        return source.readByteString().string(GB18030)
    }

    override fun close() {
        socket.close()
    }

}

fun httpRequest(
    url: String,
    content: TransactionSend<*>,
): Request {
    return Request.Builder()
        .url(url)
        .post(content.toRequestBody())
        .build()
}