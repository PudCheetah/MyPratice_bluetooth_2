package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.BroadcastManager
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo
import com.example.mypratice_bluetooth_2.IntentLauncher
import com.example.mypratice_bluetooth_2.MessageManager
import com.example.mypratice_bluetooth_2.MyBluetoothManager
import com.example.mypratice_bluetooth_2.SocketManager
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import java.util.UUID

class DeviceConsoleActivity : AppCompatActivity() {
    private val TAG = "MyTag" + DeviceConsoleActivity::class.java.simpleName
    private lateinit var binding: ActivityDeviceConsoleBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var viewModel: Viewmodel_DeviceConsole
    private lateinit var intentLauncher: IntentLauncher
    private lateinit var bluetoothAction: BluetoothAction
    private lateinit var socketManager: SocketManager
    private lateinit var messageManager: MessageManager
    private lateinit var setupUI: DeviceConsoleActivity_setupUI
    private lateinit var broadcastManager: BroadcastManager
    private val MY_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = intent.getParcelableExtra<DataClass_BluetoothDeviceInfo>("DeviceInfo")
        bluetoothAdapter = MyBluetoothManager.bluetoothAdapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceInfo?.deviceAddress)
        binding = ActivityDeviceConsoleBinding.inflate(layoutInflater)
        intentLauncher = IntentLauncher(this)
        viewModel = ViewModelProvider(this).get(Viewmodel_DeviceConsole::class.java)
        viewModel.localAddress.value = bluetoothAdapter.address
        viewModel.localAndrdoiID_VM.value = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        socketManager = SocketManager(this, viewModel, MY_UUID)
        messageManager = MessageManager(this, viewModel)
        bluetoothAction = BluetoothAction(this, bluetoothAdapter, intentLauncher)
        setupUI = DeviceConsoleActivity_setupUI(this, binding, bluetoothAction, socketManager, bluetoothDevice, viewModel, messageManager, bluetoothAdapter)
        broadcastManager = BroadcastManager(this, this, viewModel)
        setupUI.initializeUI()
        binding.fab2.setOnClickListener {
            Log.d(TAG, "testFAB: ${viewModel.localAndrdoiID_VM.value} , ${viewModel.targetAndroidID_VM.value}")
        }
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
//        viewModel.addToDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.textMessageList.value = null
    }

    fun checkSwitchStatus(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }
}