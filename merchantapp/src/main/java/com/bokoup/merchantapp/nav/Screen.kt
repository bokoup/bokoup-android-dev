package com.bokoup.merchantapp.nav

enum class Screen(val title: String) {
    Customer(title = "Customer"),
    Merchant(title = "Merchant"),
    Tender(title = "Tenders"),
    Promo(title = "Promos"),
    Settings(title = "Settings");

    var route: String = name
}

