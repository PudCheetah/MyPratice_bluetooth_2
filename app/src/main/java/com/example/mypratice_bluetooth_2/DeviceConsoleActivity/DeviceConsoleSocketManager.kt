package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

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
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class DeviceConsoleSocketManager(val context: Context, val viewModel: Viewmodel_DeviceConsole, val MY_UUID: UUID) {
    private val TAG = "MyTag" + DeviceConsoleSocketManager::class.java.simpleName
    //建立伺服器端(優畫板)
    suspend fun createBluetoothServerSocket_2(bluetoothAdapter: BluetoothAdapter){
        withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothServerSocket: Starting")
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothServerSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext
            }
            var serverSocket: BluetoothServerSocket? = null
            var clientSocket: BluetoothSocket? = null
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MY_UUID", MY_UUID)
                Log.d(TAG, "createBluetoothServerSocket: Listening for connections")
                serverSocket?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "成功建立伺服器", Toast.LENGTH_SHORT).show()
                    }
                }
                clientSocket = serverSocket.accept()
                viewModel.connectSocket.postValue(clientSocket)
                Log.d(TAG, "createBluetoothServerSocket: Connection accepted")
                clientSocket?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "成功連接", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothServerSocket: Error occurred", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "伺服器端口建立失敗", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Clean up the connection
//                try {
//                    serverSocket?.close()
//                    clientSocket?.close()
//                } catch (e: IOException) {
//                    Log.e(TAG, "createBluetoothServerSocket: Error closing sockets", e)
//                }
            }
        }
    }

    //建立客戶端(優化板)
    suspend fun createBluetoothClientSocket_2(bluetoothDevice: BluetoothDevice): Boolean{
        return withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothClientSocket: Starting")
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
                viewModel.connectSocket.postValue(socket)
                Log.d(TAG, "createBluetoothClientSocket: Connection successful")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "連接成功", Toast.LENGTH_SHORT).show()
                }
                true
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothClientSocket: Connection failed", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "連接失敗", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }
}