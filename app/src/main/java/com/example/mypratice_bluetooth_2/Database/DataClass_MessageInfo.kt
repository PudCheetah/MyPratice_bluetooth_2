package com.example.mypratice_bluetooth_2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class DataClass_MessageInfo(
    @PrimaryKey(autoGenerate = true)
    val sourceID: Long? = 0,
    val sourceAddress: String ?= "unknow Address",
    val sourceType: String,
    val time: String?,
    val message: String,
    val reciveStatus: Boolean?,
    val randomID: String?
)
