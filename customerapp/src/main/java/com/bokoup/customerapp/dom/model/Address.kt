package com.bokoup.customerapp.dom.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["active"], unique = true)])
data class Address(
    @PrimaryKey val id: String,
    val active: Boolean? = null
)
