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

class SocketManager_server(val context: Context, val viewModel: SocketManager_Interface,
                           val bluetoothAdapter: BluetoothAdapter, val MY_UUID: UUID,
                           val progressBarSet: ProgressBarSet_interface){
    private val TAG = "MyTag" + SocketManager_server::class.java.simpleName
    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null

    //建立伺服器端(ver2.4)
    suspend fun createBluetoothServerSocket_2(bluetoothAdapter: BluetoothAdapter): Boolean{
        return withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothServerSocket: Starting")
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothServerSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MY_UUID", MY_UUID)
                Log.d(TAG, "createBluetoothServerSocket: Listening for connections")
                serverSocket?.also {
                    withContext(Dispatchers.Main) {
                        progressBarSet.changeProgressText("建立伺服器，等待連線中")
//                        Toast.makeText(context, "建立伺服器，等待連線中", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d(TAG, "Before accept()")
                clientSocket = try {
                    serverSocket?.accept()
                }catch (e: IOException){
                    Log.d(TAG, "Error accepting connection")
                    null
                }
                if(clientSocket == null){
                    Log.d(TAG, "Client socket is null, failed to accept connection")
                    return@withContext false
                }
                Log.d(TAG, "After accept()")
                viewModel.updateConnectSocket(clientSocket)
                Log.d(TAG, "createBluetoothServerSocket: Connection accepted")
                clientSocket?.also {
                    withContext(Dispatchers.Main) {
                        progressBarSet.changeProgressText("成功連接")
//                        Toast.makeText(context, "成功連接", Toast.LENGTH_SHORT).show()
                    }
                }
                return@withContext true
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothServerSocket: Error occurred", e)
                withContext(Dispatchers.Main) {
                    progressBarSet.changeProgressText("伺服器端口建立失敗")
//                    Toast.makeText(context, "伺服器端口建立失敗", Toast.LENGTH_SHORT).show()
                }
                return@withContext false
            }
        }
    }

    fun stopSocket(){
        Log.d(TAG, "stopSocket()")
        try {
            if (serverSocket != null){
                serverSocket?.close()
            }
        }catch (e: IOException) {
            Log.d(TAG, "Could not close the server socket")
        }finally {
            serverSocket = null
            viewModel.stopSocket()
        }
    }
}