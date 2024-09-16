package com.example.mypratice_bluetooth_2.Database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MessageDao {

    @Upsert
    fun UpsertMessage(messageInfo: DataClass_MessageInfo)

    @Query("select * FROM DataClass_MessageInfo")
    fun getAllMessage(): MutableList<DataClass_MessageInfo>

    @Query("select * FROM DataClass_MessageInfo WHERE sourceType = :address")
    fun getAllMessageByAddress(address: String): MutableList<DataClass_MessageInfo>
    @Query("select * From DataClass_MessageInfo Where sourceType = :name")
    fun getAllMessageByName(name: String): MutableList<DataClass_MessageInfo>
}