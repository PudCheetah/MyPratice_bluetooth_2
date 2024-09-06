package com.example.mypratice_bluetooth_2.MainActivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.BroadcastManager
import com.example.mypratice_bluetooth_2.IntentLauncher
import com.example.mypratice_bluetooth_2.MyBluetoothManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ViewModel_MainActivity::class.java)
        requestPermissions(permissionArray())

        myBluetoothManager = MyBluetoothManager(this)
        bluetoothAdapter = myBluetoothManager.getBluetoothAdapter()
        intentLauncher = IntentLauncher(this)
        broadcastManager = BroadcastManager(this, this, viewModel)
        bluetoothAction = BluetoothAction(this, bluetoothAdapter, intentLauncher)

        rvSet()
        switchInitSet()

        setContentView(binding.root)
        viewModel.scannedDevices.observe(this){
            binding.rvMainActivity.adapter?.notifyDataSetChanged()
            binding.root.invalidate()
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isCheck ->
            if(isCheck){
                bluetoothAction.enableBluetooth()
            }else{
                bluetoothAction.disableBluetooth()
            }
        }
        binding.floatingActionButton3.setOnClickListener {
            btnAction_activityScanning()
            btnAction_changeBluetoothMode_discoverable()
        }
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

    fun requestPermissions(permissionArray: Array<String>){
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            permissionArray.forEach { permission ->
                val statu = when(permissions[permission]){
                    true -> {"success"}
                    false -> {"fail"}
                    else -> {"unknow"}
                }
                Log.d(TAG, "requestPermissions: ${permission}: ${statu}")
            }
        }.launch(permissionArray)
    }

    fun rvSet(){
        binding.rvMainActivity.layoutManager = LinearLayoutManager(this)
        binding.rvMainActivity.adapter = RvAdapter_mainActivity(this, viewModel, intentLauncher)
    }
    fun switchInitSet(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }
    fun btnAction_activityScanning(){
        bluetoothAction.activityScanning()
    }
    fun btnAction_changeBluetoothMode_discoverable(){
        bluetoothAction.changeBluetoothMode_discoverable()
    }
}