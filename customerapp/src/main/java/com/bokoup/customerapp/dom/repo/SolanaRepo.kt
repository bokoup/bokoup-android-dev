package com.bokoup.customerapp.dom.repo

import com.bokoup.lib.Resource
import com.dgsd.ksol.core.model.*
import kotlinx.coroutines.flow.Flow

interface SolanaRepo {

    fun signAndSend(
        transaction: LocalTransaction,
        keyPair: KeyPair
    ): Flow<Resource<TransactionSignature>>

    suspend fun airDrop(
        accountKey: PublicKey,
        lamports: Lamports = 2_000_000_000
    ): TransactionSignature
}