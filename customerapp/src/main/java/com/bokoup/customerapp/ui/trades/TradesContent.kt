package com.bokoup.customerapp.ui.trades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.ui.transaction.TransactionItem
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.TransactionSignature

@Composable
@ExperimentalMaterial3Api
fun TradesContent(
    padding: PaddingValues,
    onTradeListingClicked: (BokoupTradeListing) -> Unit,
    viewModel: TradesViewModel = hiltViewModel(),
) {

    val tradeListings by viewModel.tradeListings.collectAsState(emptyList())
    val isLoading by viewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Loading(isLoading = true)
        }
    } else if (tradeListings.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = stringResource(R.string.trades_empty_message),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (tradeListings.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = stringResource(R.string.trading_list_for_sale_title),
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = tradeListings) { listing ->
                        TradeListingItem(listing, onTradeListingClicked)
                    }
                }
            }
        }
    }
}



