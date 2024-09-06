package com.example.mypratice_bluetooth_2.MainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo

class ViewModel_MainActivity: ViewModel() {
    var scannedDevices = MutableLiveData<MutableSet<DataClass_BluetoothDeviceInfo>>()
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