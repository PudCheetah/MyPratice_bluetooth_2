package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket
import com.example.mypratice_bluetooth_2.Database.DataClass_MessageInfo

interface MessageManager_interface {
    fun addToMessageList(address: String?, source: String?, time: String?, string: String)
    fun gettextMessageList(): MutableList<DataClass_MessageInfo>?

}