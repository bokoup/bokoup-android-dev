package com.bokoup.customerapp.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bokoup.customerapp.R
import com.bokoup.customerapp.ui.transaction.TransactionItem
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.TransactionSignature

@Composable
@ExperimentalMaterial3Api
fun TransactionsContent(
    padding: PaddingValues,
    onTransactionClicked: (TransactionSignature) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {

    val transactions by viewModel.transactions.collectAsState(emptyList())

    val isLoading by viewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Loading(isLoading = true)
        }
    } else if (transactions.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = stringResource(R.string.transactions_empty_message),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = transactions) { transaction ->
                TransactionItem(transaction, onTransactionClicked)
            }
        }
    }
}



