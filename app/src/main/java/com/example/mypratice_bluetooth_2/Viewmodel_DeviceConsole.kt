package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Viewmodel_DeviceConsole: ViewModel() {
    var textList = MutableLiveData<MutableList<String>>()
    var isClient = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()

    init {
        textList.value = mutableListOf<String>()
        isClient.value = true
        connectSocket.value = null
    }
    fun addToTextList(string: String){
        textList.value?.add(string)
        textList.value = textList.value
    }
}