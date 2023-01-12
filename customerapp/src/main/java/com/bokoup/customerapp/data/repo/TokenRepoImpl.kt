package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.data.net.TokenDao
import com.bokoup.customerapp.dom.repo.TokenRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import kotlinx.coroutines.flow.Flow

class TokenRepoImpl(
    private val tokenDao: TokenDao,
    private val solPay: SolPay,
) : TokenRepo {
    override fun getTokensFromRoom() = tokenDao.getTokens()
    override fun getApiId(
        url: String,
    ): Flow<Resource<SolPayTransactionRequestDetails>> {
        return resourceFlowOf {
            solPay.getDetails(SolPayTransactionRequest(url))
        }
    }

    override fun getTokenTransaction(
        url: String,
        address: String
    ): Flow<Resource<SolPayTransactionInfo>> {
        return resourceFlowOf {
            solPay.getTransaction(
                PublicKey.fromBase58(address),
                SolPayTransactionRequest(url)
            )
        }
    }
}