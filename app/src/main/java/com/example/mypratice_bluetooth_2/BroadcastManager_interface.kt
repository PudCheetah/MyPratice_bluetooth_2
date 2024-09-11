package com.example.mypratice_bluetooth_2

import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo

interface BroadcastManager_interface {
    fun addDevice(device: DataClass_BluetoothDeviceInfo)
    fun updateSwitchStatus(isOn: Boolean)
    fun getScannedDevice(): MutableSet<DataClass_BluetoothDeviceInfo>?
    fun clearConnectSocket()
}