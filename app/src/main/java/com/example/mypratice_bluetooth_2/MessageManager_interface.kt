package com.example.mypratice_bluetooth_2

import com.example.mypratice_bluetooth_2.Database.DataClass_MessageInfo

interface MessageManager_interface {
//    fun addToMessageList(address: String?, sourceType: String?, time: String?, string: String)
    fun gettextMessageList(): MutableList<DataClass_MessageInfo>?

    fun getLocalAddress(): String?
    fun getTargetAddressFromConnectSocket(): String?
    fun findAndUpdate_textMessageList(randomMessageID: String?)
    fun updateVM_textMessageList(randomMessageID: String,sourceAddress: String?, targetAddress: String?,message: String)
    suspend fun getLocalAndrdoiID(): String?
    suspend fun getTargetAndroidID(): String?
    suspend fun updateVM_textMessageListFromDatabase(andrdoiID: String)
    suspend fun updateTargetAndroidID(targetAndroidID: String)
}