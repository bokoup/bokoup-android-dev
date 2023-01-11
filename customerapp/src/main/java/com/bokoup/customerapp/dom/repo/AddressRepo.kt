package com.bokoup.customerapp.dom.repo


import com.bokoup.customerapp.dom.model.Address
import com.bokoup.lib.Resource
import kotlinx.coroutines.flow.Flow

interface AddressRepo {
    fun insertAddress(active: Boolean? = null): Flow<Resource<Unit>>
    fun getAddresses(): Flow<Resource<List<Address>>>
    fun getActiveAddress(): Flow<Resource<Address?>>
    fun updateActive(id: String)
}