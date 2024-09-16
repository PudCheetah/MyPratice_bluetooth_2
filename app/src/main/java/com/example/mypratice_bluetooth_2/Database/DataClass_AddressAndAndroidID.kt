package com.example.mypratice_bluetooth_2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataClass_AddressAndAndroidID(
    @PrimaryKey(autoGenerate = true)
    val ID: Long? = 0,
    val address: String ?= "unknowAddress",
    val androidID: String ?= "unknowAndroidID",
    val nickName: String ?= "unknowNickName"
)
