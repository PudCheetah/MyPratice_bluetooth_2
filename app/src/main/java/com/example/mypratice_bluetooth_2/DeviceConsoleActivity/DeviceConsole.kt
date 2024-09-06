package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

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
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo
import com.example.mypratice_bluetooth_2.IntentLauncher
import com.example.mypratice_bluetooth_2.MyBluetoothManager
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
    private lateinit var intentLauncher: IntentLauncher
    private lateinit var bluetoothAction: BluetoothAction
    private lateinit var socketManager: DeviceConsoleSocketManager
    private lateinit var messageManager: MessageManager
    private val MY_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = intent.getParcelableExtra<DataClass_BluetoothDeviceInfo>("DeviceInfo")
        bluetoothAdapter = MyBluetoothManager.bluetoothAdapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceInfo?.deviceAddress)
        binding = ActivityDeviceConsoleBinding.inflate(layoutInflater)
        intentLauncher = IntentLauncher(this)
        viewModel = ViewModelProvider(this).get(Viewmodel_DeviceConsole::class.java)
        socketManager = DeviceConsoleSocketManager(this, viewModel, MY_UUID)
        messageManager = MessageManager(this, viewModel)
        bluetoothAction = BluetoothAction(this, bluetoothAdapter, intentLauncher)
        initializeUI()

        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
        viewModel.addToDatabase()
    }
    fun initializeUI(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Problem")
        }
        binding.tvDeviceName.text = bluetoothDevice?.name
        binding.tvDeviceAddress.text = bluetoothDevice?.address
        binding.tvDeviceType.text = bluetoothDevice?.type.toString()
        binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        rvSet()
        switchInitSet()
        listenterAndObserve_set()

    }
    fun listenterAndObserve_set(){
        binding.btnConnect.setOnClickListener {
            btnAction_connect()
        }
        binding.btnSendMessage.setOnClickListener {
            btnAction_sendMessage()
        }
        binding.switch1.setOnCheckedChangeListener { buttonView, isCheck ->
            if(isCheck){
                bluetoothAction.enableBluetooth()
            }else{
                bluetoothAction.disableBluetooth()
            }
        }
        viewModel.textMessageList.observe(this){
            if(viewModel.textMessageList.value?.size != 0){
                Toast.makeText(this, "${viewModel.textMessageList.value?.last()}", Toast.LENGTH_SHORT).show()
            }
            binding.rvDeviceConsole.adapter?.notifyDataSetChanged()
            binding.root.invalidate()
        }
    }



    fun btnAction_connect(){
        CoroutineScope(Dispatchers.IO).launch {
            if(socketManager.createBluetoothClientSocket_2(bluetoothDevice) == true){

            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(this@DeviceConsole, "嘗試建立伺服器", Toast.LENGTH_SHORT).show()
                }
                socketManager.createBluetoothServerSocket_2(bluetoothAdapter)
            }
            messageManager.receiveMessages(viewModel.connectSocket.value)
        }
    }
    fun btnAction_sendMessage(){
        if(bluetoothAdapter.isEnabled){
            if(viewModel.connectSocket.value != null){
                if(binding.etMessageInput.text?.isEmpty() == true){
                    Toast.makeText(this, "不可傳送空白訊息", Toast.LENGTH_SHORT).show()
                }else{
                    val message = binding.etMessageInput.text.toString()
                    messageManager.sendMessage(viewModel.connectSocket.value, message)
                    binding.etMessageInput.text?.clear()
                }
            }else{
                Toast.makeText(this, "連線尚未建立", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "請先開啟藍芽", Toast.LENGTH_SHORT).show()
        }
    }

    fun rvSet(){
        binding.rvDeviceConsole.layoutManager = LinearLayoutManager(this)
        binding.rvDeviceConsole.adapter = RvAdapter_deviceConsole(viewModel)
    }
    fun switchInitSet(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }
}