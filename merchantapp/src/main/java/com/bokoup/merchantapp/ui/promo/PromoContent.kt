package com.bokoup.merchantapp.ui.tender

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bokoup.lib.Loading
import com.bokoup.merchantapp.ui.promo.CreatePromoCard
import com.bokoup.merchantapp.ui.promo.PromoViewModel
import com.bokoup.merchantapp.ui.promo.QRCodeCard
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.channels.Channel

//https://developer.android.com/codelabs/jetpack-compose-basics#8

@Composable
@ExperimentalMaterial3Api
fun PromoContent(
    modifier: Modifier = Modifier,
    viewModel: PromoViewModel = hiltViewModel(),
    padding: PaddingValues,
    channel: Channel<String>,
    cardState: Boolean,
    setCardState: (Boolean) -> Unit,
) {
//    val promos by viewModel.promosConsumer.data.collectAsState()
    val isLoading by viewModel.isLoadingConsumer.collectAsState(false)
    val error: Throwable? by viewModel.errorConsumer.collectAsState(null)
    val uriHandler = LocalUriHandler.current
    val promos by viewModel.promosConsumer.data.collectAsState()
    val qrCode by viewModel.qrCodeConsumer.data.collectAsState()
    val groupSeed by viewModel.groupSeedConsumer.data.collectAsState()
    val keyPair by viewModel.keyPairConsumer.data.collectAsState()
    val createdPromoResult by viewModel.createPromoConsumer.data.collectAsState()
    val signature by viewModel.signatureConsumer.data.collectAsState()

    val (mintCardState, setMintCardState) = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(createdPromoResult) {
        if (createdPromoResult != null && keyPair != null) {
            viewModel.signAndSend(createdPromoResult!!.transaction, keyPair!!)
        }
    }

    LaunchedEffect(signature) {
        Log.d("PromoContent", signature.toString())
    }

    LaunchedEffect(Unit) {
        viewModel.fetchPromos()
        viewModel.getKeyPair()
    }

    LaunchedEffect(error) {
        if (error != null) {
            channel.trySend(error!!.message.toString())
        }
    }

    LaunchedEffect(promos) {
        setMintCardState(false)
    }

    Loading(promos == null)

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .padding(padding)
    ) {
        if ((promos != null) && !isLoading) {
            val chunks = promos!!.chunked(4)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                chunks.map {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        it.map { promo ->
                            val link =
                                "https://explorer.solana.com/address/" + promo.promo.mintObject?.id + "?cluster=devnet"
                            val attributes =
                                JsonParser.parseString(Gson().toJson(promo.promo.metadataObject?.attributes)).asJsonArray.filter {
                                    !listOf<String>(
                                        "maxMint",
                                        "maxBurn"
                                    ).contains(it.asJsonObject["trait_type"].asString)
                                }

                            ElevatedCard(
                                modifier = Modifier
                                    .width(350.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        viewModel.getQrCode(
                                            promo.promo.mintObject?.id!!,
                                            "Approve to receive promo ${promo.promo.metadataObject?.name!!}"
                                        )
                                        setMintCardState(true)
                                    }
                            ) {
                                Column(
                                    modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                PaddingValues(
                                                    start = 16.dp,
                                                    end = 16.dp,
                                                    top = 8.dp
                                                )
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = promo.promo.metadataObject!!.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        TextButton(onClick = { uriHandler.openUri(link) }) {
                                            Text(text = promo.promo.mintObject?.id?.slice(0..8) ?: "")
                                        }
                                    }
                                    AsyncImage(
                                        model = promo.promo.metadataObject?.image,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .width(324.dp),
                                        contentDescription = null
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Text(
                                            text = promo.promo.metadataObject?.description.toString(),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Divider(thickness = 1.dp)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "issued:",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = promo.promo.mintCount.toString() + " / " + promo.promo.maxMint.toString(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "redeemed:",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = promo.promo.burnCount.toString() + " / " + promo.promo.maxBurn.toString(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Divider(thickness = 1.dp)
                                    }
                                    attributes.map{ a ->
                                        val attr = a.asJsonObject
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = attr["trait_type"].asString,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                            Text(
                                                text = attr["value"].asString,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                }
                            }

                        }
                    }
                }
            }

        }
        if (cardState && keyPair != null && groupSeed != null) {
            CreatePromoCard(setCardState = setCardState,
                createPromo = {promo, uri, memo, _, _ -> viewModel.createPromo(promo, uri, memo, keyPair!!.publicKey.toString(), groupSeed!!)} )
        }
        if (mintCardState && qrCode != null) {
            QRCodeCard(
                setCardState = setMintCardState,
                qrCode = qrCode!!
            )
        }
    }
}