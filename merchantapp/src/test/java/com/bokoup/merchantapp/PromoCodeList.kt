package com.bokoup.merchantapp
import com.bokoup.merchantapp.util.addAttribute
import com.google.gson.JsonArray
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PromoCodeList {
    @Test
    fun promoCodeList_Correctly()
    {
        val attributes = JsonArray()
        attributes.addAttribute("value", "buyXCurrencyGetYPercent")
        attributes.addAttribute("trait_type", "promoType")
        attributes.addAttribute("trait_type", "buyXCurrency")
        attributes.addAttribute("trait_type", "getYPercent")
        val metadataObject = PromoListQuery.MetadataObject(
            id = "Ek2qTxcNG7D7MXotcN9GTRQcLT4YGv2HC8PGRrvRBkXP",
            name = "Test Promo",
            symbol = "BTP",
            uri = "https://arweave.net/SHYtRcUoOQgK6IK6eylhpQFwWJRT06ILci1GYMH8u-Y",
            image = "https://arweave.net/xzk50ncvFZlQ1DU9k7Hp84QTagiffoCa4DDFUSEH4DA",
            description = "This test promo gives you 10 percent off if you spend more than $1.00.",
            attributes = attributes)

        val mintObject=PromoListQuery.MintObject(
            id = "6Q2t5egrBZJQSJ3nyHkRP544KGHfDsQpDjxy1W5ehnsA",
            supply =7
        )

        val promo=PromoListQuery.Promo(
            id = "AM7tRfSZeBTPW8RgLSU9bwv7esfoBxFwUSfEANGYx35P",
            owner = "2Com52sP8R8y43eSA6oLaVEEfHF48Vx9szavvyBGiHCC",
            maxMint = 10,
            maxBurn = 5,
            mintCount = 10,
            burnCount = 3,
            createdAt = "2022-11-08:07:00.775456+00:00",
            metadataObject = metadataObject,
            mintObject = mintObject!!
        )
       assertEquals(promo.id , "AM7tRfSZeBTPW8RgLSU9bwv7esfoBxFwUSfEANGYx35P")
         assert(promo.metadataObject==metadataObject)

    }

}