package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket

interface SocketManager_Interface {
    suspend fun updateConnectSocket(socket: BluetoothSocket?)
}