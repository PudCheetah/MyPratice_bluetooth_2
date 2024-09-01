package com.example.mypratice_bluetooth_2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.ContextParams
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import org.jetbrains.annotations.TestOnly

class BluetoothAction(val context: Context, val bluetoothAdapter: BluetoothAdapter, val intentLauncher: IntentLauncher) {
    private val TAG = "MyTag" + BluetoothAction::class.java.simpleName


    //開啟藍芽
    fun enableBluetooth(){
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        Log.d(TAG, "activityBluetooth: activityBluetooth()")
        intentLauncher.activityIntent(intent)
    }
    //關閉藍芽
    fun disableBluetooth(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "disableBluetooth: permiss problem")
        }
        bluetoothAdapter.disable()
    }

    //啟動藍芽搜尋
    fun activityScanning(){
        if(context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "activityScanning: Permission Problem")
        }
        if(bluetoothAdapter.isDiscovering){
            Toast.makeText(context, "搜尋已開始", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "activityScanning: Scanning has been started")
        }else{
            if(bluetoothAdapter.startDiscovery()){
                Toast.makeText(context, "搜尋開始", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "activityScanning: start scanning")
            }else{
                Log.d(TAG, "activityScanning: scanning start Fail")
            }
        }
    }
    //關閉藍芽搜尋
    fun inactivityScanning(){
        if(context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "inactivityScanning: permission problem")
        }
        bluetoothAdapter.cancelDiscovery()
    }

    //使裝置進入可被搜尋狀態
    fun changeBluetoothMode_discoverable(){
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        intentLauncher.activityIntent(intent)
    }

}