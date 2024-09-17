package com.example.mypratice_bluetooth_2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class DataClass_MessageInfo(
    val sourceAndroidID: String ?= "unknowSourceAndroidID",
    val targetAndroidID: String ?= "unknowTargetAndroidID",
    val sourceType: String,
    val time: String?,
    val message: String,
    val reciveStatus: Boolean?,
    @PrimaryKey
    val randomID: String
)
