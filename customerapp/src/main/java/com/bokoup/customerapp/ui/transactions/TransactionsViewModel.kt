package com.bokoup.customerapp.ui.transactions

import androidx.lifecycle.ViewModel
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val dataRepo: DataRepo,
    private val addressRepo: AddressRepo,
) : ViewModel() {
    

}