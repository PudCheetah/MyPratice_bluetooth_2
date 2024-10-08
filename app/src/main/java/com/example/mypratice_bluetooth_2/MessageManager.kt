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
import java.io.InputStream
import java.util.UUID

class MessageManager(val context: Context, val viewModel: MessageManager_interface) {
    private val TAG = "MyTag" + MessageManager::class.java.simpleName

    //傳送訊息
    fun sendMessage(socket: BluetoothSocket?, message: String?, needPacking: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream = socket?.outputStream
                var newMessage = ""
                if(needPacking == true){
                    newMessage = packingMessage(message) ?: "unknow"
                    Log.d(TAG, "sendMessage: needPacking == true")
                }else{
                    newMessage = message ?: "unknow"
                    Log.d(TAG, "sendMessage: needPacking == false")
                }
                outputStream?.write(newMessage?.toByteArray())
                outputStream?.flush()
                Log.d(TAG, "Message sent: $newMessage")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending message", e)
            }
        }
    }
    //接收訊息
    fun receiveMessages(socket: BluetoothSocket?) {
        if(socket == null){
            Log.d(TAG, "Socket is null in receiveMessages")
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "receiveMessages: Permission Problem")
                    }
                    withContext(Dispatchers.Main){
//                        Toast.makeText(context, "開始監聽", Toast.LENGTH_SHORT).show()
                    }
                    val inputStream = socket?.inputStream
                    val buffer = ByteArray(1024) // 用來存儲接收的數據
                    while (isActive) {
                        // 從輸入流中讀取數據
                        messageListener(inputStream, buffer, socket)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error receiving message", e)
                }
            }
        }
    }

    //監聽程序
    suspend fun messageListener(inputStream: InputStream?, buffer:  ByteArray, socket: BluetoothSocket?){
        val bytes = inputStream?.read(buffer)
        val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
        val newMessage = unpackingMessage(message)
        if (newMessage != null){
            if(isReply(newMessage) == false){
                Log.d(TAG, "isReply(unpackingMessage) == false -> ${newMessage}")
                sendReply(socket, newMessage?.get(0))
                withContext(Dispatchers.Main){
                    viewModel.updateVM_textMessageList(newMessage.get(0), newMessage.get(1),newMessage.get(2), newMessage.get(3))
                }
            }else{
                Log.d(TAG, "isReply(unpackingMessage) == true -> ${newMessage}")
                withContext(Dispatchers.Main){
                    Log.d(TAG, "isReply(newMessage) == true -> unpackingMessage: ${newMessage.get(0)}")
                    viewModel.findAndUpdate_textMessageList(newMessage.get(0))
                }
            }
        }
        Log.d(TAG, "Message received: $message")
    }
    //加工訊息
    suspend fun packingMessage(message: String?): String?{
        var processedMessage: String? = null
        val randomMessageID = UUID.randomUUID().toString()
        val splitSymbo = "|!@#|"
        val sourceAndroidID = viewModel.getLocalAndrdoiID()
        val targetAndroidID = viewModel.getTargetAndroidID()
        withContext(Dispatchers.Main){
            viewModel.updateVM_textMessageList(randomMessageID, sourceAndroidID, targetAndroidID, message?: "unknow")
        }
        processedMessage = "${randomMessageID}" + "${splitSymbo}" + "${sourceAndroidID}" + "${splitSymbo}" + "${targetAndroidID}" + "${splitSymbo}" + "${message}"
        return processedMessage
    }
    //解包訊息並輸出成串列
    fun unpackingMessage(message: String?): List<String>?{
        val messageList = message?.split("|!@#|")
        return messageList
    }

    //判斷訊息是否為已讀確認
    fun isReply(list: List<String>?): Boolean {
        return list?.size == 1
    }
    fun sendReply(socket: BluetoothSocket?, randomMessageID: String?){
        sendMessage(socket, randomMessageID, false)
    }
    //傳送身分驗證訊息
    fun sendAuthenticationMessage(socket: BluetoothSocket?){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = viewModel.getLocalAndrdoiID()
                val outputStream = socket?.outputStream
                Log.d(TAG, "sendAuthenticationMessage: ${message}")
                outputStream?.write(message?.toByteArray())
                outputStream?.flush()
            }catch (e: IOException){
                Log.e(TAG, "Error sending message", e)
            }
        }
    }
    //接收身分驗證訊息並放入viewModel中
    fun receiveAuthenticationMessage(socket: BluetoothSocket?){
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
                val bytes = inputStream?.read(buffer)
                val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
                Log.d(TAG, "receiveAuthenticationMessage: ${message}")
                withContext(Dispatchers.Main){
                    viewModel.updateTargetAndroidID(message!!)
                    viewModel.updateVM_textMessageListFromDatabase(message)
                }
                Log.d(TAG, "receiveAuthenticationMessage_ViewModel: ${viewModel.getTargetAndroidID()}")
                Log.d(TAG, "Message received: $message")
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving message", e)
            }
        }
    }

}