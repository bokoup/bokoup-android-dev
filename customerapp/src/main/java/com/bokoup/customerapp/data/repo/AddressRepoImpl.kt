package com.bokoup.customerapp.data.repo

import android.content.SharedPreferences
import androidx.annotation.CheckResult
import androidx.core.content.edit
import com.bokoup.customerapp.data.net.AddressDao
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.AddressWithPrivateKey
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import com.bokoup.lib.mapData
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import kotlinx.coroutines.flow.Flow
import javax.inject.Provider

private const val SHARED_PREF_PRIVATE_KEY_PREFIX = "private_key_"

class AddressRepoImpl(
    private val addressDao: AddressDao,
    private val solanaRepo: SolanaRepo,
    private val keyFactory: KeyFactory,
    private val secretsSharedPreferencesProvider: Provider<SharedPreferences>,
) : AddressRepo {

    @CheckResult
    override fun insertAddress(active: Boolean?) = resourceFlowOf {
        insertAddressPrep(active)
    }

    override fun getAddresses(): Flow<Resource<List<Address>>> {
        return addressDao.getAddresses().asResourceFlow()
    }

    override fun getActiveAddress(): Flow<Resource<Address?>> {
        return addressDao.getActiveAddress().asResourceFlow()
    }

    override fun getActiveAddressWithPrivateKey(): Flow<Resource<AddressWithPrivateKey?>> {
        return getActiveAddress().mapData { activeAddress ->
            if (activeAddress == null) {
                null
            } else {
                val publicKey = PublicKey.fromBase58(activeAddress.id)
                val privateKeyString = secretsSharedPreferencesProvider.get().getString(
                    createPrivateKeySharedPrefKey(publicKey),
                    null
                )

                AddressWithPrivateKey(
                    address = activeAddress,
                    privateKey = PrivateKey.fromBase58(checkNotNull(privateKeyString))
                )
            }
        }
    }

    override fun updateActive(id: String) {
        addressDao.clearActive()
        addressDao.setActive(id)
    }

    private suspend fun insertAddressPrep(active: Boolean?) {
        val words = keyFactory.createMnemonic(MnemonicPhraseLength.TWELVE)
        val newKeyPair = keyFactory.createKeyPairFromMnemonic(words)

        secretsSharedPreferencesProvider.get().edit {
            putString(
                createPrivateKeySharedPrefKey(newKeyPair.publicKey),
                newKeyPair.privateKey.toBase58String()
            )
        }

        val address = Address(id = newKeyPair.publicKey.toString(), active = active)

        addressDao.insertAddress(address)
        solanaRepo.airDrop(newKeyPair.publicKey)
    }

    private fun createPrivateKeySharedPrefKey(publicKey: PublicKey): String {
        return "$SHARED_PREF_PRIVATE_KEY_PREFIX${publicKey.toBase58String()}"
    }
}