package com.bokoup.merchantapp

import com.bokoup.lib.QRCodeGenerator.generateQR
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URL
import java.net.URLEncoder

@RunWith(RobolectricTestRunner::class)
class GetQrCode {
    @Test
    fun getQrCode() {
        runBlocking {
            val mintString="6Q2t5egrBZJQSJ3nyHkRP544KGHfDsQpDjxy1W5ehnsA"
            val message="Approve to receive promo Test Promo"
            val url = URL("https://tx.api.bokoup.dev/promo/mint/$mintString")
            URLEncoder.encode(
                message,
                "utf-8"
            )
            val content = "$url"
            assert(content == "https://tx.api.bokoup.dev/promo/mint/${mintString}")

        }

    }
}