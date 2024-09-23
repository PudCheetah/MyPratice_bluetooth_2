package com.example.mypratice_bluetooth_2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mypratice_bluetooth_2.DeviceConsoleActivity.ProgressBarSet_interface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class SocketManager_client(val context: Context, val viewModel: SocketManager_Interface, val MY_UUID: UUID, val progressBarSet: ProgressBarSet_interface) {
    private val TAG = "MyTag" + SocketManager_server::class.java.simpleName
    //建立客戶端(優化板)
    suspend fun createBluetoothClientSocket_2(bluetoothDevice: BluetoothDevice): Boolean{
        return withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothClientSocket: Starting")
            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
                viewModel.updateConnectSocket(socket)
                Log.d(TAG, "createBluetoothClientSocket: Connection successful")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "連接成功", Toast.LENGTH_SHORT).show()
                }
                true
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothClientSocket: Connection failed", e)
                withContext(Dispatchers.Main) {
                    progressBarSet.changeProgressText("嘗試連線中")
//                    Toast.makeText(context, "連接失敗", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }
}