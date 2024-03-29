package com.bokoup.customerapp.data.net

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bokoup.customerapp.dom.model.Address
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM address ORDER BY id ASC")
    fun getAddresses(): Flow<List<Address>>

    @Query("SELECT * FROM address WHERE active")
    fun getActiveAddress(): Flow<Address?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddresses(tokens: List<Address>)

    @Query("UPDATE address SET active = NULL WHERE active")
    fun clearActive()

    @Query("UPDATE address SET active = TRUE WHERE id = :id")
    fun setActive(id: String)
}

