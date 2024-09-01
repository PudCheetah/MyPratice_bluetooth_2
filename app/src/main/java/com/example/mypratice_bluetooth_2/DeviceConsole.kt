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
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class DeviceConsole : AppCompatActivity() {
    private val TAG = "MyTag" + DeviceConsole::class.java.simpleName
    private lateinit var binding: ActivityDeviceConsoleBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private val MY_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = intent.getParcelableExtra<BluetoothDeviceInfo>("DeviceInfo")
        bluetoothAdapter = MyBluetoothManager(this).getBluetoothAdapter()
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceInfo?.deviceAddress)
        binding = ActivityDeviceConsoleBinding.inflate(layoutInflater)

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            binding.tvDeviceName.text = bluetoothDevice?.name
            binding.tvDeviceAddress.text = bluetoothDevice?.address
            binding.tvDeviceType.text = bluetoothDevice?.type.toString()
            binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        }
        binding.btnConnect.setOnClickListener {
            btnAction_connect()
        }

        setContentView(binding.root)
    }

    fun btnAction_connect(){
        CoroutineScope(Dispatchers.IO).launch {
            if(createBluetoothClientSocket_2() == false){
                createBluetoothServerSocket_2()
            }
        }
    }

    //建立伺服器端
    fun createBluetoothServerSocket(){
        CoroutineScope(Dispatchers.IO).launch {
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
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
                            Toast.makeText(this@DeviceConsole, "連線已建立", Toast.LENGTH_SHORT).show()
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
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothServerSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext
            }
            var serverSocket: BluetoothServerSocket? = null
            var clientSocket: BluetoothSocket? = null

            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MY_UUID", MY_UUID)
                Log.d(TAG, "createBluetoothServerSocket: Listening for connections")

                clientSocket = serverSocket.accept()
                Log.d(TAG, "createBluetoothServerSocket: Connection accepted")

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole, "連線已建立", Toast.LENGTH_SHORT).show()
                }
                // Here you can start communication with the client
                // For example, you could call a function to handle the connection:
                // handleClientConnection(clientSocket)
            } catch (e: IOException) {
                Log.e(TAG, "createBluetoothServerSocket: Error occurred", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DeviceConsole, "連線失敗", Toast.LENGTH_SHORT).show()
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
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
            }else{
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                if(socket != null){
                    try {
                        socket.connect()
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DeviceConsole, "連接成功", Toast.LENGTH_SHORT).show()
                        }
                        Log.d(TAG, "creatBluetoothClientSocket: 連接成功")
                    }catch (e: IOException){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DeviceConsole, "連接失敗", Toast.LENGTH_SHORT).show()
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
            if (ActivityCompat.checkSelfPermission(this@DeviceConsole,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "createBluetoothClientSocket: Missing BLUETOOTH_CONNECT permission")
                return@withContext false
            }
            try {
                val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
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
    fun sendMessage(socket: BluetoothSocket, message: String) {
        try {
            val outputStream = socket.outputStream
            outputStream.write(message.toByteArray())
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)
        }
    }
    //接收訊息
    fun receiveMessage(socket: BluetoothSocket): String {
        val inputStream = socket.inputStream
        val buffer = ByteArray(1024)
        val bytes = inputStream.read(buffer)
        return String(buffer, 0, bytes)
    }
}