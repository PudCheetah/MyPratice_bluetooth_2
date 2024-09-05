package com.example.mypratice_bluetooth_2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class DeviceConsole : AppCompatActivity() {
    private val TAG = "MyTag" + DeviceConsole::class.java.simpleName
    private lateinit var binding: ActivityDeviceConsoleBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var viewModel: Viewmodel_DeviceConsole
    private val MY_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = intent.getParcelableExtra<DataClass_BluetoothDeviceInfo>("DeviceInfo")
        bluetoothAdapter = MyBluetoothManager.bluetoothAdapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceInfo?.deviceAddress)
        binding = ActivityDeviceConsoleBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(Viewmodel_DeviceConsole::class.java)
        rvSet()

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            binding.tvDeviceName.text = bluetoothDevice?.name
            binding.tvDeviceAddress.text = bluetoothDevice?.address
            binding.tvDeviceType.text = bluetoothDevice?.type.toString()
            binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        }
        binding.btnConnect.setOnClickListener {
            btnAction_connect()
        }
        binding.btnSendMessage.setOnClickListener {
            btnAction_sendMessage()
        }
        viewModel.textMessageList.observe(this){
            if(viewModel.textMessageList.value?.size != 0){
                Toast.makeText(this, "${viewModel.textMessageList.value?.last()}", Toast.LENGTH_SHORT).show()
            }
            binding.rvDeviceConsole.adapter?.notifyDataSetChanged()
//            rvSet()
            binding.root.invalidate()
        }
        setContentView(binding.root)
    }

    fun btnAction_connect(){
        CoroutineScope(Dispatchers.IO).launch {
            if(createBluetoothClientSocket_2() == true){

            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(this@DeviceConsole, "嘗試建立伺服器", Toast.LENGTH_SHORT).show()
                }
                createBluetoothServerSocket_2()
            }
            receiveMessages(viewModel.connectSocket.value)
        }
    }
    fun btnAction_sendMessage(){
        if(viewModel.connectSocket.value != null){
            if(binding.etMessageInput.text?.isEmpty() == true){
                Toast.makeText(this, "不可傳送空白訊息", Toast.LENGTH_SHORT).show()
            }else{
                val message = binding.etMessageInput.text.toString()
                sendMessage(viewModel.connectSocket.value, message)
                binding.etMessageInput.text?.clear()
            }
        }else{
            Toast.makeText(this, "連線尚未建立", Toast.LENGTH_SHORT).show()
        }

    }




    //建立伺服器端(優畫板)
    suspend fun createBluetoothServerSocket_2(){
        withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothServerSocket: Starting")
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(this@DeviceConsole, "成功建立伺服器", Toast.LENGTH_SHORT).show()
                    }
                }
                clientSocket = serverSocket.accept()
                viewModel.connectSocket.postValue(clientSocket)
                Log.d(TAG, "createBluetoothServerSocket: Connection accepted")
                clientSocket?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DeviceConsole, "成功連接", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothServerSocket: Error occurred", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole, "伺服器端口建立失敗", Toast.LENGTH_SHORT).show()
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
    suspend fun createBluetoothClientSocket_2(): Boolean{
        return withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothClientSocket: Starting")
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
                viewModel.connectSocket.postValue(socket)
                Log.d(TAG, "createBluetoothClientSocket: Connection successful")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole, "連接成功", Toast.LENGTH_SHORT).show()
                }
                true
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothClientSocket: Connection failed", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole, "連接失敗", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }


    //傳送訊息
    //傳送訊息
    fun sendMessage(socket: BluetoothSocket?, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream = socket?.outputStream
                withContext(Dispatchers.Main){
                    viewModel.addToMessageList(null, null, message)
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
                if (ActivityCompat.checkSelfPermission(this@DeviceConsole,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "receiveMessages: Permission Problem")
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(this@DeviceConsole, "開始監聽", Toast.LENGTH_SHORT).show()
                }
                val inputStream = socket?.inputStream
                val deviceName = socket?.remoteDevice?.name ?: "unknow"
                val buffer = ByteArray(1024) // 用來存儲接收的數據
                while (isActive) {
                    // 從輸入流中讀取數據
                    val bytes = inputStream?.read(buffer)
                    val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
                    Log.d(TAG, "Message received: $message")
                    message?.also {
                        withContext(Dispatchers.Main){
                            viewModel.addToMessageList(deviceName, null, message)
                            Log.d(TAG, "receiveMessages: ${viewModel.textMessageList.value}")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving message", e)
            }
        }
    }
    fun rvSet(){
        binding.rvDeviceConsole.layoutManager = LinearLayoutManager(this)
        binding.rvDeviceConsole.adapter = RvAdapter_deviceConsole(viewModel)
    }
}