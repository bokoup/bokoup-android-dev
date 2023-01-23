package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.MintByIdQuery
import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.TradeListingsSubscription
import com.bokoup.customerapp.data.net.DataService
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.BokoupTokenInfo
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.customerapp.fragment.MintInfoFragment
import com.bokoup.customerapp.fragment.PromoTransactionFragment
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import com.bokoup.lib.mapData
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class DataRepoImpl(private val dataService: DataService) : DataRepo {

    override fun getTokensOwnedByAccountSubscription(
        address: Address
    ): Flow<Resource<List<TokenAccountListSubscription.TokenAccount>>> {
        return dataService
            .getTokensOwnedByAccountSubscription(address)
            .toFlow()
            .map { it.dataAssertNoErrors.tokenAccount }
            .asResourceFlow()
    }

    override fun getTransactionsByAccount(
        address: Address
    ): Flow<Resource<List<BokoupTransaction>>> {
        return dataService
            .getTransactionsByAccount(address)
            .toFlow()
            .map { it.dataAssertNoErrors }
            .map { data ->
                data.promoTransactions
                    .map { it.promoTransactionFragment }
                    .map { checkNotNull(createTransaction(it)) }
                    .sortedByDescending { it.timestamp }
            }
            .asResourceFlow()
    }

    override fun getTradeListings(): Flow<Resource<List<BokoupTradeListing>>> {
        return dataService
            .getTradeListings()
            .toFlow()
            .map { it.dataAssertNoErrors }
            .map { data ->
                data.listingWithToken
                    .map { checkNotNull(createTradeListing(it)) }
                    .sortedByDescending { it.timestamp }

            }
            .asResourceFlow()

    }

    override fun getTransactionByAddressAndToken(
        address: Address,
        token: PublicKey,
    ): Flow<Resource<List<BokoupTransaction>>> {
        return getTransactionsByAccount(address)
            .mapData { allTransactions ->
                allTransactions.filter {
                    it.tokenInfo.address == token
                }
            }
    }

    override fun getMintTokenInfo(mint: PublicKey): Flow<Resource<MintByIdQuery.Mint>> {
        return dataService.getTokenByMintId(mint.toBase58String())
            .toFlow()
            .map { it.dataAssertNoErrors }
            .map { it.mint.single() }
            .asResourceFlow()
    }

    override fun getTransaction(
        transactionSignature: TransactionSignature
    ): Flow<Resource<BokoupTransaction>> {
        return dataService
            .getTransactionBySignature(transactionSignature)
            .toFlow()
            .map { it.dataAssertNoErrors }
            .map { it.promoTransactions.single().promoTransactionFragment }
            .map { checkNotNull(createTransaction(it)) }
            .asResourceFlow()
    }

    private fun createTradeListing(
        data: TradeListingsSubscription.ListingWithToken
    ): BokoupTradeListing? {
        if (data.id == null) {
            return null
        }

        if (data.seller == null) {
            return null
        }

        if (data.tokenSize == null) {
            return null
        }

        val price = (data.price as? Int)?.toFloat()?.toCurrentBigDecimal()
        if (price == null) {
            return null
        }

        val mintId = data.mintObject?.mintInfoFragment?.id
        if (mintId == null) {
            return null
        }

        val metadataObject = data.mintObject.mintInfoFragment.promoObject?.metadataObject
        if (metadataObject == null) {
            return null
        }

        return BokoupTradeListing(
            id = data.id,
            tokenInfo = createTokenInfo(mintId, metadataObject),
            tokenSize = checkNotNull(data.tokenSize as Int),
            sellerAddress = checkNotNull(data.seller),
            price = price,
            timestamp = OffsetDateTime.ofInstant(
                Instant.ofEpochSecond((data.createdAtOnChain as Int).toLong()),
                ZoneId.systemDefault()
            )
        )
    }

    private fun createTransaction(
        data: PromoTransactionFragment
    ): BokoupTransaction? {
        if (data.signature == null) {
            return null
        }

        if (data.merchantName == null) {
            return null
        }

        val mintId = data.mintObject?.mintInfoFragment?.id
        if (mintId == null) {
            return null
        }

        val metadataObject = data.mintObject.mintInfoFragment.promoObject?.metadataObject
        if (metadataObject == null) {
            return null
        }

        val type = when (data.transactionType) {
            "mint" -> BokoupTransaction.Type.RECEIVED
            "burn" -> BokoupTransaction.Type.REDEEMED
            else -> null
        }

        if (type == null) {
            return null
        }

        return BokoupTransaction(
            signature = data.signature,
            type = type,
            merchantName = data.merchantName,
            orderId = data.orderId as? String,
            paymentId = data.paymentId as? String,
            orderTotal = (data.orderTotal as? Float)?.toCurrentBigDecimal(),
            discountValue = (data.discountValue as? Float)?.toCurrentBigDecimal(),
            timestamp = OffsetDateTime.parse(data.modifiedAt as String),
            tokenInfo = createTokenInfo(mintId, metadataObject)
        )
    }

    private fun createTokenInfo(
        mintId: String,
        data: MintInfoFragment.MetadataObject
    ): BokoupTokenInfo {
        return BokoupTokenInfo(
            address = PublicKey.fromBase58(mintId),
            name = data.name,
            symbol = data.symbol,
            imageUrl = data.image as String,
            description = data.description as String?
        )
    }

    private fun Float.toCurrentBigDecimal(): BigDecimal {
        return toBigDecimal().movePointLeft(2)
    }

}