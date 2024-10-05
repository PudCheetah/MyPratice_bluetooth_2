package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothSocket
import androidx.room.Dao
import com.example.mypratice_bluetooth_2.Database.MessageDao

interface SocketManager_Interface {
    suspend fun updateConnectSocket(socket: BluetoothSocket?)
    suspend fun getLocalAndrdoiID(): String?
    suspend fun getTargetAndroidID(): String?
}