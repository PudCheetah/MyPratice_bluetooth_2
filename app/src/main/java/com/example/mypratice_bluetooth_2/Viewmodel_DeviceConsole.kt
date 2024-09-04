package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Viewmodel_DeviceConsole: ViewModel() {
    var textSendingList = MutableLiveData<MutableList<String>>()
    var textReceiveList = MutableLiveData<MutableList<String>>()

    var isClient = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()

    init {
        textSendingList.value = mutableListOf<String>()
        textReceiveList.value = mutableListOf<String>()
        isClient.value = true
        connectSocket.value = null
    }
    fun addToReceiveList(string: String){
        textReceiveList.value?.add(string)
        textReceiveList.value = textReceiveList.value
    }
    fun addToSendingList(string: String){
        textSendingList.value?.add(string)
        textSendingList.value = textSendingList.value
    }
}