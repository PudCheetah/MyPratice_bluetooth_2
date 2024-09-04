package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel_MainActivity: ViewModel() {
    var scannedDevices = MutableLiveData<MutableSet<DataClass_BluetoothDeviceInfo>>()
    var scannedDevices_2 = MutableLiveData<MutableSet<BluetoothDevice>>()
    var switchStatu = MutableLiveData<Boolean>()

    init {
        scannedDevices.value = mutableSetOf()
        switchStatu.value = false
    }

    fun addDevice(deviceInfo: DataClass_BluetoothDeviceInfo){
        scannedDevices.value?.add(deviceInfo)
        scannedDevices.value = scannedDevices.value
    }
}