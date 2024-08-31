package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import android.widget.Toast

class MyBluetoothManager(val context: Context) {
    private val TAG = "MyTag" + MyBluetoothManager::class.java.simpleName
    init {
        getBluetoothManagerAndAdapter()
    }

    companion object{
        lateinit var bluetoothManager: BluetoothManager
        lateinit var bluetoothAdapter: BluetoothAdapter
    }


    fun getBluetoothManagerAndAdapter(){
        try {
            bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            if(bluetoothAdapter != null){
                Toast.makeText(context, "支援藍芽", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "不支援藍芽", Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Log.e("BluetoothError", "Error getting Bluetooth adapter", e)
            Toast.makeText(context, "不支援藍芽B", Toast.LENGTH_LONG).show()
        }
    }
    fun getBluetoothAdapter(): BluetoothAdapter{
        return bluetoothAdapter
    }
}