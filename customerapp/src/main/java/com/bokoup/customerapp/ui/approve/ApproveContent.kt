package com.bokoup.customerapp.ui.approve

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.ui.common.SwipeButton
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import java.net.URLDecoder


@Composable
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
fun ApproveContent(
    padding: PaddingValues,
    appId: SolPayTransactionRequestDetails?,
    message: String?,
    isComplete: Boolean,
    signature: TransactionSignature?,
    onSwipe: () -> Unit,
    swipeComplete: Boolean,
    setSwipeComplete: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (appId?.label != null && appId.iconUrl != null) {
            Text(text = appId.label.orEmpty(), modifier = Modifier.padding(vertical = 16.dp))
            AsyncImage(
                model = appId.iconUrl,
                placeholder = painterResource(id = R.drawable.ic_bokoup_logo),
                contentDescription = null,
            )
        }
        if (message != null) {
            if (!swipeComplete) {
                Text(text = URLDecoder.decode(message, "utf-8"), modifier = Modifier.padding(vertical = 16.dp))
            }
            Row(modifier = Modifier.height(64.dp)) {
                SwipeButton(
                    text = "swipe to approve",
                    isComplete = isComplete,
                    isSuccess = signature != null,
                    onSwipe = onSwipe,
                    swipeComplete = swipeComplete,
                    setSwipeComplete = setSwipeComplete,
                )
            }
            if (signature != null) {
                Text(
                    text = "Confirmed: $signature",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

    }
}