package com.bokoup.customerapp.data.net

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bokoup.customerapp.dom.model.Address

@Database(
    entities = [Address::class], version = 1, exportSchema = false
)
abstract class ChainDb : RoomDatabase() {
    abstract fun addressDao(): AddressDao
}
