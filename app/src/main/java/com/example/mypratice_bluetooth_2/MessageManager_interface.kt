package com.example.mypratice_bluetooth_2

import com.example.mypratice_bluetooth_2.Database.DataClass_MessageInfo

interface MessageManager_interface {
    fun addToMessageList(address: String?, sourceType: String?, time: String?, string: String)
    fun gettextMessageList(): MutableList<DataClass_MessageInfo>?

    fun getLocalAddress(): String?
    fun getTargetAddress(): String?
    fun findAndUpdate_textMessageList(randomMessageID: String?)
    fun updateVM_textMessageList(randomMessageID: String, sourceAddress: String?, message: String)
}