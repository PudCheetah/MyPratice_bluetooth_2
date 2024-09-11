package com.example.mypratice_bluetooth_2.MainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypratice_bluetooth_2.BroadcastManager_interface
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo

class ViewModel_MainActivity: ViewModel(), BroadcastManager_interface {
    var scannedDevices = MutableLiveData<MutableSet<DataClass_BluetoothDeviceInfo>>()
    var switchStatu = MutableLiveData<Boolean>()

    init {
        scannedDevices.value = mutableSetOf()
        switchStatu.value = false
    }

    override fun addDevice(deviceInfo: DataClass_BluetoothDeviceInfo) {
        scannedDevices.value?.add(deviceInfo)
        scannedDevices.value = scannedDevices.value
    }

    override fun updateSwitchStatus(isOn: Boolean) {
        switchStatu.value = isOn
    }

    override fun getScannedDevice(): MutableSet<DataClass_BluetoothDeviceInfo>? {
        return scannedDevices.value
    }


}