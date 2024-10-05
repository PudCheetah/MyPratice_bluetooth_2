package com.example.mypratice_bluetooth_2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo
import com.example.mypratice_bluetooth_2.DeviceConsoleActivity.ProgressBarSet

class BroadcastManager(val context: Context, val activity: AppCompatActivity, val viewModel: BroadcastManager_interface) {
    private val TAG = "MyTag" + BroadcastManager::class.java.simpleName

    init {
        BroadcastReceiverSet(intentFilter())
    }

    fun intentFilter(): IntentFilter{
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        return filter
    }

    fun BroadcastReceiverSet(intentFilter: IntentFilter){
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent?.action){
                    BluetoothDevice.ACTION_FOUND -> { broadcastAction_ACTION_FOUND(intent)}
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> { broadcastAction_ACTION_DISCOVERY_STARTED()}
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> { broadcastAction_ACTION_DISCOVERY_FINISHED()}
                    BluetoothAdapter.ACTION_STATE_CHANGED -> { broadcastAction_ACTION_STATE_CHANGED(intent) }
                }
            }
        }
        activity.registerReceiver(receiver, intentFilter)
    }
    fun broadcastAction_ACTION_FOUND(intent: Intent){
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "broadcastAction_ACTION_FOUND: permission problem")
        }
        val deviceName = device?.name
        val deviceAddress = device?.address
        val deviceType = device?.type
        val deviceUUID = device?.uuids.toString()
        val deviceInfo = DataClass_BluetoothDeviceInfo(deviceName, deviceAddress, deviceType, deviceUUID)
        viewModel.addDevice(deviceInfo)
        Log.d(TAG, "ACTION_FOUND: ${deviceName}: ${deviceAddress}")
        Log.d(TAG, "broadcastAction_ACTION_FOUND(ViewModel): ${viewModel.getScannedDevice()}")
    }
    fun broadcastAction_ACTION_DISCOVERY_STARTED(){
        Toast.makeText(context, "Discovery started", Toast.LENGTH_LONG).show()
        Log.d(TAG, "Discovery started")
    }
    fun broadcastAction_ACTION_DISCOVERY_FINISHED(){
        Toast.makeText(context, "Discovery finished", Toast.LENGTH_LONG).show()
        Log.d(TAG, "Discovery finished")
    }
    fun broadcastAction_ACTION_STATE_CHANGED(intent: Intent){
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        when(state){
            BluetoothAdapter.STATE_ON -> { viewModel.updateSwitchStatus(true)}
            BluetoothAdapter.STATE_OFF -> {
                viewModel.updateSwitchStatus(false)
                viewModel.clearConnectSocket()
            }
        }
    }
}