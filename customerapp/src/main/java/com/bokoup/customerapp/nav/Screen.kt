package com.bokoup.customerapp.nav

import androidx.annotation.StringRes
import com.bokoup.customerapp.R


enum class Screen(@StringRes val title: Int) {
    Tokens(title = R.string.tokens),
    TokenDetail(title = R.string.token_detail),
    Wallet(title = R.string.wallet),
    Transactions(title = R.string.transactions),
    Transaction(title = R.string.transaction),
    Share(title = R.string.share),
    Scan(title = R.string.scan),
    Approve(title = R.string.approve),
}

