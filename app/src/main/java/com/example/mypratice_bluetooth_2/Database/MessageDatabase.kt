package com.example.mypratice_bluetooth_2.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DataClass_AddressAndAndroidID::class, DataClass_MessageInfo::class), version = 1)
abstract class MessageDatabase: RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object{
        private var instance: MessageDatabase ?= null

        fun getInstance(context: Context ?= null): MessageDatabase?{
            if(instance != null){
                return instance
            }else{
                instance = Room.databaseBuilder(context!!, MessageDatabase::class.java, "MessageDatabase").build()
                return instance
            }
        }
    }
}