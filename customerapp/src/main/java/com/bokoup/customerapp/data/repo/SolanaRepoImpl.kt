package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SolanaRepoImpl(
    private val solanaApi: SolanaApi,
    private val localTransactions: LocalTransactions,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SolanaRepo {

    override fun signAndSend(
        transaction: LocalTransaction,
        keyPair: KeyPair
    ): Flow<Resource<TransactionSignature>> = resourceFlowOf {
        solanaApi.sendTransaction(localTransactions.sign(transaction, keyPair))
    }

    override suspend fun airDrop(accountKey: PublicKey, lamports: Lamports): TransactionSignature =
        withContext(dispatcher) {
            solanaApi.requestAirdrop(accountKey, lamports)
        }
}