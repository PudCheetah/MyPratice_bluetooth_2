package com.example.mypratice_bluetooth_2

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class IntentLauncher(private val activity: AppCompatActivity) {
    private val TAG = "MyTag" + IntentLauncher::class.java.simpleName
    private lateinit var launcher: ActivityResultLauncher<Intent>

    init {
        intentLauncher()
    }

    private fun intentLauncher(): ActivityResultLauncher<Intent>{
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                Log.d(TAG, "initLauncher: Activity.RESULT_OK")
            }else{
                Log.d(TAG, "intentLauncher: Fail")
            }
        }
        return launcher
    }

    fun activityIntent(intent: Intent){
        launcher.launch(intent)
    }
}