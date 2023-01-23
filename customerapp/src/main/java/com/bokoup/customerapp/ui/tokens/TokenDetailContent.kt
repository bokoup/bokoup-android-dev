package com.bokoup.customerapp.ui.tokens

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.ui.transaction.TransactionItem
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.TransactionSignature
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun tokenDetailViewModelFactory(): TokenDetailViewModel.Factory
}

@Composable
@ExperimentalMaterial3Api
fun TokenDetailContent(
    padding: PaddingValues,
    tokenId: String,
    onTransactionClicked: (TransactionSignature) -> Unit,
) {

    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).tokenDetailViewModelFactory()

    val viewModel = viewModel(
        factory = TokenDetailViewModel.provideFactory(factory, tokenId)
    ) as TokenDetailViewModel

    val uriHandler = LocalUriHandler.current

    val mintInfo by viewModel.mintInfo.collectAsState(null)

    val transactions by viewModel.transaction.collectAsState(emptyList())

    val isLoading by viewModel.isLoading.collectAsState(false)

    val errorMessage by viewModel.error.collectAsState(null)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Loading(isLoading = true)
        }
    } else if (!errorMessage.isNullOrEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = checkNotNull(errorMessage),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else if (mintInfo != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = mintInfo?.mintInfoFragment?.promoObject?.metadataObject?.name.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = mintInfo?.mintInfoFragment?.promoObject?.metadataObject?.description?.toString()
                    .orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.size(32.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.token_details_mint_address),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = mintInfo?.mintInfoFragment?.id.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        uriHandler.openUri(
                            "https://explorer.solana.com/address/" + mintInfo?.mintInfoFragment?.id + "?cluster=devnet"
                        )
                    },
            )

            if (transactions.isNotEmpty()) {
                Spacer(modifier = Modifier.size(32.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = stringResource(R.string.token_details_my_history_title),
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = transactions) { transaction ->
                        TokenDetailTransaction(transaction, onTransactionClicked)
                    }
                }
            }
        }
    }
}


