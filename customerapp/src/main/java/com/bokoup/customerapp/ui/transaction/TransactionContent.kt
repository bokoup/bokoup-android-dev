package com.bokoup.customerapp.ui.transaction

import android.app.Activity
import android.icu.text.NumberFormat
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun transactionViewModelFactory(): TransactionViewModel.Factory
}


@Composable
@ExperimentalMaterial3Api
fun TransactionContent(
    padding: PaddingValues,
    transactionSignature: TransactionSignature,
    openTokenDetails: (PublicKey) -> Unit,
) {

    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).transactionViewModelFactory()

    val viewModel = viewModel(
        factory = TransactionViewModel.provideFactory(factory, transactionSignature)
    ) as TransactionViewModel

    val uriHandler = LocalUriHandler.current

    val transaction by viewModel.transaction.collectAsState(null)

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
    } else if (transaction != null) {
        val txn = checkNotNull(transaction)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = txn.tokenInfo.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            openTokenDetails.invoke(txn.tokenInfo.address)
                        },
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = Italic,
                    text = when (txn.type) {
                        BokoupTransaction.Type.RECEIVED -> stringResource(
                            R.string.transaction_received_description_template,
                            txn.tokenInfo.name
                        )
                        BokoupTransaction.Type.REDEEMED -> stringResource(
                            R.string.transaction_redeemed_description_template,
                            txn.tokenInfo.name
                        )
                    }
                )
            }

            TitleAndValue(
                title = stringResource(R.string.transaction_details_title_merchant),
                value = txn.merchantName
            )

            if (txn.orderId != null) {
                TitleAndValue(
                    title = stringResource(R.string.transaction_details_title_order_id),
                    value = checkNotNull(txn.orderId)
                )
            }

            if (txn.paymentId != null) {
                TitleAndValue(
                    title = stringResource(R.string.transaction_details_title_payment_id),
                    value = checkNotNull(txn.paymentId)
                )
            }

            if (txn.orderTotal != null) {
                TitleAndValue(
                    title = stringResource(R.string.transaction_details_title_order_total),
                    value = NumberFormat.getCurrencyInstance().format(checkNotNull(txn.orderTotal))
                )
            }

            if (txn.discountValue != null) {
                TitleAndValue(
                    title = stringResource(R.string.transaction_details_title_discount_value),
                    value = NumberFormat.getCurrencyInstance().format(checkNotNull(txn.discountValue))
                )
            }

            TitleAndValue(
                title = stringResource(R.string.transaction_details_title_date),
                value = DateUtils.formatDateTime(
                    LocalContext.current,
                    txn.timestamp.toEpochSecond() * 1000,
                    DateUtils.FORMAT_ABBREV_ALL or
                            DateUtils.FORMAT_SHOW_WEEKDAY or
                            DateUtils.FORMAT_SHOW_DATE or
                            DateUtils.FORMAT_SHOW_TIME
                )
            )


            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.transaction_details_title_signature),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.size(4.dp))

            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = txn.signature,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable {
                    uriHandler.openUri(
                        "https://explorer.solana.com/tx/" + txn.signature + "?cluster=devnet"
                    )
                }
            )
        }
    }
}

@Composable
private fun TitleAndValue(
    title: String,
    value: String,
) {
    Spacer(modifier = Modifier.size(16.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
    )
    Spacer(modifier = Modifier.size(4.dp))
    Text(
        style = MaterialTheme.typography.bodyLarge,
        text = value
    )
}



