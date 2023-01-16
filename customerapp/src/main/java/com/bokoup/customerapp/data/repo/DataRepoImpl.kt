package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.data.net.DataService
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.customerapp.fragment.PromoTransactionFragment
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
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

    private fun createTransaction(
        data: PromoTransactionFragment
    ): BokoupTransaction? {
        if (data.signature == null) {
            return null
        }

        if (data.merchantName == null) {
            return null
        }

        val metadataObject = data.mintObject?.promoObject?.metadataObject
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
            timestamp = OffsetDateTime.parse(data.modifiedAt as String),
            tokenInfo = createTokenInfo(metadataObject)
        )
    }

    private fun createTokenInfo(
        data: PromoTransactionFragment.MetadataObject
    ): BokoupTransaction.TokenInfo {
        return BokoupTransaction.TokenInfo(
            address = PublicKey.fromBase58(data.id),
            name = data.name,
            symbol = data.symbol,
            imageUrl = data.image as String
        )
    }
}