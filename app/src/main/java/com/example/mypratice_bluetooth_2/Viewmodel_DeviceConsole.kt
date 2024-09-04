package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Viewmodel_DeviceConsole: ViewModel() {
    var textMessageList = MutableLiveData<MutableList<DataClass_MessageInfo>>()

    var isClient = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()

    init {
        textMessageList.value = mutableListOf()
        isClient.value = true
        connectSocket.value = null
    }
    fun addToMessageList(source: String?, time: String?, string: String){
        textMessageList.value?.add(DataClass_MessageInfo(source ?: "local", time ?: null,string))
        textMessageList.value = textMessageList.value
    }
//    fun addToSendingList(string: String){
//        textSendingList.value?.add(DataClass_Message("local", null, string))
//        textSendingList.value = textSendingList.value
//    }
}