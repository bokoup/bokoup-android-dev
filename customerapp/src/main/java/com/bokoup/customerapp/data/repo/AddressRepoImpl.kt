package com.bokoup.customerapp.data.repo

import androidx.annotation.CheckResult
import com.bokoup.customerapp.data.net.AddressDao
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import kotlinx.coroutines.flow.Flow

class AddressRepoImpl(
    private val addressDao: AddressDao,
    private val solanaRepo: SolanaRepo,
    private val keyFactory: KeyFactory
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

    override fun updateActive(id: String) {
        addressDao.clearActive()
        addressDao.setActive(id)
    }

    private suspend fun insertAddressPrep(active: Boolean?) {
        val words = keyFactory.createMnemonic(MnemonicPhraseLength.TWELVE)
        val newKeyPair = keyFactory.createKeyPairFromMnemonic(words)

        val address = Address(
            id = newKeyPair.publicKey.toString(),
            phrase = words.joinToString(),
            active = active
        )
        addressDao.insertAddress(address)
        solanaRepo.airDrop(newKeyPair.publicKey)
    }

}