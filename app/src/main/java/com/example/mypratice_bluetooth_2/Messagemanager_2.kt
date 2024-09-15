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
import java.util.UUID

class Messagemanager_2(val context: Context, val viewModel: MessageManager_interface) {
    private val TAG = "MyTag" + MessageManager::class.java.simpleName

    //傳送訊息
    fun sendMessage(socket: BluetoothSocket?, message: String?, needPacking: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream = socket?.outputStream
                var newMessage = ""
                if(needPacking == true){
                    newMessage = packingMessage(message) ?: "unknow"
                }else{
                    newMessage = message ?: "unknow"
                }
                outputStream?.write(newMessage?.toByteArray())
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
                if (ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "receiveMessages: Permission Problem")
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "開始監聽", Toast.LENGTH_SHORT).show()
                }
                val inputStream = socket?.inputStream
                val buffer = ByteArray(1024) // 用來存儲接收的數據
                while (isActive) {
                    // 從輸入流中讀取數據
                    val bytes = inputStream?.read(buffer)
                    val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
                    val unpackingMessage = unpackingMessage(message)
                    if (unpackingMessage != null){
                        if(isReply(unpackingMessage) == true){
                            viewModel.findAndUpdate_textMessageList(unpackingMessage.get(1))
                        }else{
                            sendReply(socket, unpackingMessage?.get(1))
                            viewModel.updateVM_textMessageList(
                                unpackingMessage.get(1),
                                unpackingMessage.get(0),
                                unpackingMessage.get(2),
                                unpackingMessage.get(3)
                            )
                        }
                    }
                    Log.d(TAG, "Message received: $message")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving message", e)
            }
        }
    }
    //加工訊息
    fun packingMessage(message: String?): String?{
        var processedMessage: String? = null
        val randomMessageID = UUID.randomUUID().toString()
        val splitSymbo = "|!@#|"
        val sourceAddress = viewModel.getLocalAddress()
        processedMessage = "${sourceAddress}" + "${splitSymbo}" + "${randomMessageID}" + "${splitSymbo}" + "${message}"

        return processedMessage
    }
    //解包訊息並輸出成串列
    fun unpackingMessage(message: String?): List<String>?{
        val messageList = message?.split("|!@#|")
        return messageList
    }

    fun isReply(list: List<String>?): Boolean {
        return list?.size == 1
    }
    fun sendReply(socket: BluetoothSocket?, randomMessageID: String?){
        sendMessage(socket, randomMessageID, false)
    }

}