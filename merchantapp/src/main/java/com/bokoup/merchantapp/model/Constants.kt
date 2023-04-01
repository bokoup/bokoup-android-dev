package com.bokoup.merchantapp.model

enum class EnvironmentOptions {
    HOME {
         override fun get() =
            Constants(
                "http://10.0.0.23:8080",
                "https://shining-sailfish-15.hasura.app/v1/graphql",
                "http://10.0.0.23:8899",
                "ws://10.0.0.23:8899"
            )
         },
    WORK {
        override fun get() =
            Constants(
                "http://99.91.8.130:8080",
                "https://shining-sailfish-15.hasura.app/v1/graphql",
                "http://99.91.8.130:8899",
                "ws://99.91.8.130:8899"
            )
    },
    DEPLOY {
        override fun get() =
            Constants(
                "string",
                "aling",
                "along",
                "alot"
            )
    };
    abstract fun get(): Constants
}
data class Constants(
    val apiTx: String,
    val apiData: String,
    val rpcUrl: String,
    val webSocketUrl: String,
)