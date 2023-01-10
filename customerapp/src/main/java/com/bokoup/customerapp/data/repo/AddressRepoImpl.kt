package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.data.net.AddressDao
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.resourceFlowOf
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import kotlinx.coroutines.flow.Flow

class AddressRepoImpl(
    private val addressDao: AddressDao,
    private val solanaRepo: SolanaRepo,
    private val keyFactory: KeyFactory
) : AddressRepo {

    override fun insertAddress(active: Boolean?) = resourceFlowOf {
        insertAddressPrep(active)
    }

    override fun getAddresses() = resourceFlowOf {
        var addresses = addressDao.getAddresses()
        if (addresses.isEmpty()) {
            insertAddressPrep(true)
            addresses = addressDao.getAddresses()
        }
        addresses
    }

    override fun getActiveAddress(): Flow<Resource<Address>> {
      return resourceFlowOf {
          checkNotNull(addressDao.getActiveAddress())
      }
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