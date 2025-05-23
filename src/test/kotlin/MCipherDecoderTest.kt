import moe.lasoleil.toolkit.ccb.ebs5.send.RefundSend
import moe.lasoleil.toolkit.codec.MCipherDecoder
import okio.Buffer
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class MCipherDecoderTest {

    @Test
    fun test() {
        val key = "f6528d5c335b7092fc9ec1b3020111"
        val str = "梅九六|6214662020019275"
        val cipherdURL = "AWWo2KKeATj6XxRglo7uaR0yZ2QQtCW%2C"

        MCipherDecoder.setKey(key)
        val decodedString = MCipherDecoder.decode(cipherdURL)
        assertEquals(str, decodedString)
    }

    @Test
    fun test2() {
        val requestBody = RefundSend()
            .money(BigDecimal("100.00"))
            .custId("123456789012345678901")
            .userId("123456789012345678901")
            .requestSn("0001")
            .password("abcdef")
            .order("123456789012345678901234567890")
            .toRequestBody()
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        println(buffer.readUtf8())
    }
}
