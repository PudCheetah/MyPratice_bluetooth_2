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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class DeviceConsole_2 : AppCompatActivity() {
    private val TAG = "MyTag" + DeviceConsole::class.java.simpleName
    private lateinit var binding: ActivityDeviceConsoleBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var viewModel: Viewmodel_DeviceConsole
    private val MY_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = intent.getParcelableExtra<BluetoothDeviceInfo>("DeviceInfo")
        bluetoothAdapter = MyBluetoothManager.bluetoothAdapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceInfo?.deviceAddress)
        binding = ActivityDeviceConsoleBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(Viewmodel_DeviceConsole::class.java)

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            binding.tvDeviceName.text = bluetoothDevice?.name
            binding.tvDeviceAddress.text = bluetoothDevice?.address
            binding.tvDeviceType.text = bluetoothDevice?.type.toString()
            binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        }
        binding.btnConnect.setOnClickListener {
            btnAction_connect()
        }
//        binding.btnSendMessage.setOnClickListener {
//            if (binding.etMessageInput.text?.isNotEmpty() == true){
//                val string = binding.etMessageInput.text.toString()
//                sendMessage(viewModel.connectSocket.value, string)
//            }else{
//                Toast.makeText(this, "錯誤訊息不可為空", Toast.LENGTH_SHORT).show()
//            }
//        }

        setContentView(binding.root)
    }

    fun btnAction_connect(){
        CoroutineScope(Dispatchers.IO).launch {
            if(createBluetoothClientSocket_2() == true){

            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(this@DeviceConsole_2, "嘗試建立伺服器", Toast.LENGTH_SHORT).show()
                }
                createBluetoothServerSocket_2()
            }
//            receiveMessages(viewModel.connectSocket.value)
        }
    }

    //建立伺服器端
    fun createBluetoothServerSocket(){
        CoroutineScope(Dispatchers.IO).launch {
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole_2, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                val serverSocket: BluetoothServerSocket? = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MY_UUID", MY_UUID)
                var shouldLoop = true
                while (shouldLoop) {
                    val socket: BluetoothSocket? = try {
                        serverSocket?.accept()
                    } catch (e: IOException) {
                        Log.e(TAG, "Socket's accept() method failed", e)
                        shouldLoop = false
                        null
                    }
                    socket?.also {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DeviceConsole_2, "連線已建立", Toast.LENGTH_SHORT).show()
                        }
                        Log.d(TAG, "creatBluetoothServerSocket: 連線已建立")
                        serverSocket?.close()
                        shouldLoop = false
                    }
                }
            }
        }
    }
    //建立伺服器端(優畫板)
    suspend fun createBluetoothServerSocket_2(){
        withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothServerSocket: Starting")
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole_2, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(this@DeviceConsole_2, "成功建立伺服器", Toast.LENGTH_SHORT).show()
                    }
                }
                clientSocket = serverSocket.accept()
                viewModel.connectSocket.value = clientSocket
                Log.d(TAG, "createBluetoothServerSocket: Connection accepted")
                clientSocket?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DeviceConsole_2, "成功連接", Toast.LENGTH_SHORT).show()
                    }
                }

                // Here you can start communication with the client
                // For example, you could call a function to handle the connection:
                // handleClientConnection(clientSocket)
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothServerSocket: Error occurred", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole_2, "伺服器端口建立失敗", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Clean up the connection
                try {
                    serverSocket?.close()
                    clientSocket?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "createBluetoothServerSocket: Error closing sockets", e)
                }
            }
        }
    }
    //建立客戶端
    fun createBluetoothClientSocket(): Boolean{
        Log.d(TAG, "creatBluetoothClientSocket: creatBluetoothClientSocket()")
        var isSocket = false
        CoroutineScope(Dispatchers.IO).launch {
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole_2,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
            }else{
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                if(socket != null){
                    try {
                        socket.connect()
                        viewModel.connectSocket.value = socket
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DeviceConsole_2, "成功連接至伺服器", Toast.LENGTH_SHORT).show()
                        }
                        Log.d(TAG, "creatBluetoothClientSocket: 成功連接至伺服器")
                    }catch (e: IOException){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DeviceConsole_2, "連接失敗", Toast.LENGTH_SHORT).show()
                        }
                        Log.d(TAG, "creatBluetoothClientSocket: 連接失敗")
                    }
                    isSocket = true
                }
            }
        }
        return isSocket
    }
    //建立客戶端(優化板)
    suspend fun createBluetoothClientSocket_2(): Boolean{
        return withContext(Dispatchers.IO){
            Log.d(TAG, "createBluetoothClientSocket: Starting")
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole_2,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
                Log.d(TAG, "createBluetoothClientSocket: Connection successful")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole_2, "連接成功", Toast.LENGTH_SHORT).show()
                }
                true
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothClientSocket: Connection failed", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole_2, "連接失敗", Toast.LENGTH_SHORT).show()
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
                val inputStream = socket?.inputStream
                val buffer = ByteArray(1024) // 用來存儲接收的數據
                while (isActive) {
                    // 從輸入流中讀取數據
                    val bytes = inputStream?.read(buffer)
                    val message = bytes?.let { String(buffer, 0, it) } // 將數據轉換為字符串
                    Log.d(TAG, "Message received: $message")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving message", e)
            }
        }
    }
}