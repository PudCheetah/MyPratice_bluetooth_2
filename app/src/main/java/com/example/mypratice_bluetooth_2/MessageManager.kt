package com.example.mypratice_bluetooth_2

import android.Manifest
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MessageManager(val context: Context, val viewModel: MessageManager_interface) {
    private val TAG = "MyTag" + MessageManager::class.java.simpleName

    //傳送訊息
    fun sendMessage(socket: BluetoothSocket?, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream = socket?.outputStream
                withContext(Dispatchers.Main){
                    val address = socket?.remoteDevice?.address
                    viewModel.addToMessageList(address, null, null, message)
                }
                outputStream?.write(message.toByteArray())
                outputStream?.flush()
                Log.d(TAG, "Message sent: $message")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending message", e)
            }
        }
    }
    //接收訊息
    fun receiveMessages(socket: BluetoothSocket?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "receiveMessages: Permission Problem")
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "開始監聽", Toast.LENGTH_SHORT).show()
                }
                val inputStream = socket?.inputStream
                val deviceInfo = socket?.remoteDevice
                val deviceAddress = deviceInfo?.address ?: "unknow"
                val deviceName = deviceInfo?.name ?: "unknow"
                val buffer = ByteArray(1024) // 用來存儲接收的數據
                while (isActive) {
                    // 從輸入流中讀取數據
                    val bytes = inputStream?.read(buffer)
                    val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
                    Log.d(TAG, "Message received: $message")
                    message?.also {
                        withContext(Dispatchers.Main){
                            viewModel.addToMessageList(deviceAddress, deviceName, null, message)
                            Log.d(TAG, "receiveMessages: ${viewModel.gettextMessageList()}")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving message", e)
            }
        }
    }
}