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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
        binding.btnServer.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                creatBluetoothServerSocket()
            }
        }
        binding.btnClient.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                creatBluetoothClientSocket()
            }
        }




        setContentView(binding.root)
    }
    //建立伺服器端
    fun creatBluetoothServerSocket(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
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
                    //開始通訊
                    Toast.makeText(this, "連線已建立", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "creatBluetoothServerSocket: 連線已建立")
                    serverSocket?.close()
                    shouldLoop = false
                }
            }
        }
    }
    //建立客戶端
    fun creatBluetoothClientSocket(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.cancelDiscovery()
            val socket: BluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
            socket?.let {
                socket.connect()
                Toast.makeText(this, "連接成功", Toast.LENGTH_SHORT).show()
            }
        }

    }
}