package com.example.mypratice_bluetooth_2.MainActivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.BroadcastManager
import com.example.mypratice_bluetooth_2.IntentLauncher
import com.example.mypratice_bluetooth_2.MyBluetoothManager
import com.example.mypratice_bluetooth_2.PermissionManager
import com.example.mypratice_bluetooth_2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val TAG = "MyTag" + MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel_MainActivity
    private lateinit var intentLauncher: IntentLauncher
    private lateinit var bluetoothAction: BluetoothAction
    private lateinit var myBluetoothManager: MyBluetoothManager
    private lateinit var broadcastManager: BroadcastManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listenerandobserve: MainActivity_setupUI
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ViewModel_MainActivity::class.java)
        permissionManager = PermissionManager(this)
        permissionManager.requestPermissions(permissionArray())

        myBluetoothManager = MyBluetoothManager(this)
        bluetoothAdapter = myBluetoothManager.getBluetoothAdapter()
        intentLauncher = IntentLauncher(this)
        broadcastManager = BroadcastManager(this, this, viewModel)
        bluetoothAction = BluetoothAction(this, bluetoothAdapter, intentLauncher)
        listenerandobserve = MainActivity_setupUI(this, viewModel, binding, bluetoothAction, intentLauncher)


        listenerandobserve.setupListenersAndObservers()
        checkSwitchStatus()
        setContentView(binding.root)
    }

    fun permissionArray(): Array<String>{
        var permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN

        )
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions += arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        }
        return permissions
    }

    fun checkSwitchStatus(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }
}