package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.TransactionsByAccountQuery
import com.bokoup.customerapp.data.net.DataService
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.customerapp.fragment.PromoObjectFields
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import com.dgsd.ksol.core.model.PublicKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

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
            .getTransactonsByAccount(address)
            .toFlow()
            .map { it.dataAssertNoErrors }
            .map { data ->
                val mintTransactions = data.mintPromoToken.map { createMintTransaction(it) }
                val burnTransactions =
                    data.burnDelegatedPromoToken.map { createBurnTransaction(it) }

                (mintTransactions + burnTransactions).sortedByDescending { it.timestamp }
            }
            .asResourceFlow()

    }

    private fun createBurnTransaction(
        data: TransactionsByAccountQuery.BurnDelegatedPromoToken
    ): BokoupTransaction {
        val metadataObject =
            checkNotNull(data.mintObject?.promoObject?.promoObjectFields?.metadataObject)
        return BokoupTransaction(
            signature = data.signature,
            type = BokoupTransaction.Type.REDEEMED,
            merchantName = data.merchantName,
            timestamp = OffsetDateTime.parse(data.modifiedAt as String),
            tokenInfo = createTokenInfo(metadataObject)
        )
    }

    private fun createMintTransaction(
        data: TransactionsByAccountQuery.MintPromoToken,
    ): BokoupTransaction {
        val metadataObject =
            checkNotNull(data.mintObject?.promoObject?.promoObjectFields?.metadataObject)
        return BokoupTransaction(
            signature = data.signature,
            type = BokoupTransaction.Type.RECEIVED,
            merchantName = data.merchantName,
            timestamp = OffsetDateTime.parse(data.modifiedAt as String),
            tokenInfo = createTokenInfo(metadataObject)
        )
    }

    private fun createTokenInfo(
        data: PromoObjectFields.MetadataObject
    ): BokoupTransaction.TokenInfo {
        return BokoupTransaction.TokenInfo(
            address = PublicKey.fromBase58(data.id),
            name = data.name,
            symbol = data.symbol,
            imageUrl = data.image as String
        )
    }
}