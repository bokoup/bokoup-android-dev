package com.bokoup.customerapp.ui.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.flatMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TokensViewModel @Inject constructor(
    private val dataRepo: DataRepo,
    addressRepo: AddressRepo,
) : ViewModel() {

    private val tokenAccountsResourceConsumer =
        ResourceFlowConsumer<List<TokenAccountListSubscription.TokenAccount>>(viewModelScope)

    val tokenAccounts = tokenAccountsResourceConsumer.data.map { tokenAccount ->
        tokenAccount.orEmpty().filter { token ->
            token.amount is Int && token.amount > 0
        }
    }

    init {
        tokenAccountsResourceConsumer.collectFlow(
            addressRepo.getActiveAddress()
                .flatMapSuccess { address ->
                    if (address == null) {
                        flowOf(Resource.Success(emptyList()))
                    } else {
                        dataRepo.getTokensOwnedByAccountSubscription(address)
                    }
                }
        )
    }
}