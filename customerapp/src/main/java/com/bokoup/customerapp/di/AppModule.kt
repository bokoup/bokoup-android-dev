package com.bokoup.customerapp.di

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.apollographql.apollo3.ApolloClient
import com.bokoup.customerapp.applock.AppLockManager
import com.bokoup.customerapp.applock.AppLockManagerImpl
import com.bokoup.customerapp.biometrics.AppLockBiometricManager
import com.bokoup.customerapp.biometrics.AppLockBiometricManagerImpl
import com.bokoup.customerapp.data.net.*
import com.bokoup.customerapp.data.repo.AddressRepoImpl
import com.bokoup.customerapp.data.repo.DataRepoImpl
import com.bokoup.customerapp.data.repo.SolanaRepoImpl
import com.bokoup.customerapp.data.repo.TokenRepoImpl
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.customerapp.dom.repo.TokenRepo
import com.bokoup.customerapp.util.QRCodeScanner
import com.bokoup.lib.QRCodeGenerator
import com.bokoup.lib.SystemClipboard
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.Cluster
import com.dgsd.ksol.keygen.KeyFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

private const val SHARED_PREF_NAME_SETTINGS = "settings"
private const val SHARED_PREF_APP_SECRETS = "secrets"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun apolloClient(
    ): ApolloClient = ApolloClient.Builder()
        .serverUrl("https://data.api.bokoup.dev/v1/graphql")
        .webSocketServerUrl("https://data.api.bokoup.dev/v1/graphql")
        .build()

    @Singleton
    @Provides
    fun dataService(
        apolloClient: ApolloClient
    ): DataService = DataService(apolloClient)

    @Singleton
    @Provides
    fun dataRepo(
        dataService: DataService,
    ): DataRepo = DataRepoImpl(dataService)

    @Singleton
    @Provides
    fun chainDb(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        ChainDb::class.java,
        "token"
    ).allowMainThreadQueries().build()
    // total friggin hack because I am idiot

    @Singleton
    @Provides
    fun tokenDao(
        chainDb: ChainDb,
    ) = chainDb.tokenDao()

    @Singleton
    @Provides
    fun tokenApi(
    ) = TransactionService()

    @Singleton
    @Provides
    fun solanaApi(
    ) = SolanaApi(
        Cluster.Custom("https://api.devnet.solana.com/", "wss://api.devnet.solana.com/"),
        OkHttpClient.Builder().apply {
            addInterceptor(interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build()
    )

    @Singleton
    @Provides
    fun localTransactions(
    ) = LocalTransactions

    @Singleton
    @Provides
    fun tokenRepo(
        tokenDao: TokenDao,
        transactionService: TransactionService,
    ): TokenRepo = TokenRepoImpl(
        tokenDao = tokenDao,
        transactionService = transactionService,
    )

    @Singleton
    @Provides
    fun solanaRepo(
        solanaApi: SolanaApi,
        localTransactions: LocalTransactions,
    ): SolanaRepo = SolanaRepoImpl(
        solanaApi = solanaApi,
        localTransactions = localTransactions
    )

    @Singleton
    @Provides
    fun addressDao(
        chainDb: ChainDb,
    ) = chainDb.addressDao()

    @Singleton
    @Provides
    fun addressRepo(
        addressDao: AddressDao,
        solanaRepo: SolanaRepo,
        keyFactory: KeyFactory,
        @Named(SHARED_PREF_APP_SECRETS) secretsSharedPreferencesProvider: Provider<SharedPreferences>,
    ): AddressRepo = AddressRepoImpl(
        addressDao = addressDao,
        solanaRepo = solanaRepo,
        keyFactory = keyFactory,
        secretsSharedPreferencesProvider = secretsSharedPreferencesProvider,
    )

    @Singleton
    @Provides
    fun qRCodeGenerator(
    ) = QRCodeGenerator

    @Singleton
    @Provides
    fun keyFactory(
    ) = KeyFactory

    @Singleton
    @Provides
    fun qRCodeScanner(
    ) = QRCodeScanner

    @Singleton
    @Provides
    fun systemClipboard(
        @ApplicationContext
        context: Context
    ) = SystemClipboard(context)

    @Singleton
    @Provides
    fun appLockBiometricManager(
        @ApplicationContext
        context: Context
    ): AppLockBiometricManager = AppLockBiometricManagerImpl(context)

    @Singleton
    @Provides
    @Named(SHARED_PREF_APP_SECRETS)
    fun appSecretsSharedPreferences(
        @ApplicationContext
        context: Context,
        biometricManager: AppLockBiometricManager,
    ): SharedPreferences {
        val keySpec = if (biometricManager.isAvailableOnDevice()) {
            biometricManager.createKeySpec()
        } else {
            MasterKeys.AES256_GCM_SPEC
        }

        return createSharedPreferences(context, SHARED_PREF_APP_SECRETS, keySpec)
    }

    @Singleton
    @Provides
    @Named(SHARED_PREF_NAME_SETTINGS)
    fun settingsSharedPreferences(
        @ApplicationContext
        context: Context
    ) = createSharedPreferences(context, SHARED_PREF_NAME_SETTINGS, MasterKeys.AES256_GCM_SPEC)

    @Singleton
    @Provides
    fun appLockManager(
        @Named(SHARED_PREF_NAME_SETTINGS) sharedPreferences: SharedPreferences,
    ): AppLockManager = AppLockManagerImpl(sharedPreferences)

    private fun createSharedPreferences(
        context: Context,
        fileName: String,
        keyGenParameterSpec: KeyGenParameterSpec,
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            fileName,
            MasterKeys.getOrCreate(keyGenParameterSpec),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}