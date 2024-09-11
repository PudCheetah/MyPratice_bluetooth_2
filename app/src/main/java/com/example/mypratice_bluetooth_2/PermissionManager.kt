package com.example.mypratice_bluetooth_2

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mypratice_bluetooth_2.MainActivity.MainActivity

class PermissionManager(val activity: AppCompatActivity) {
    private val TAG = "MyTag" + PermissionManager::class.java.simpleName
    fun requestPermissions(permissionArray: Array<String>){
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
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
}