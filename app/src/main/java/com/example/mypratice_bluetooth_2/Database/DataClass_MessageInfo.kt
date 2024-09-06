package com.example.mypratice_bluetooth_2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataClass_MessageInfo(
    @PrimaryKey(autoGenerate = true)
    val sourceID: Long? = 0,
    val sourceAddress: String ?= "unknow Address",
    val sourceName: String,
    val time: String?,
    val message: String
)
