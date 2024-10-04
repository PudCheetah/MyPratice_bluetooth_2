package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.content.Context
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.mypratice_bluetooth_2.DeviceConsoleActivity_initializeCallBack
import com.example.mypratice_bluetooth_2.R
import com.example.mypratice_bluetooth_2.SocketManager_Interface
import com.example.mypratice_bluetooth_2.SocketManager_server

class ProgressBarSet(val context: Context, val activity: AppCompatActivity): ProgressBarSet_interface {
    private val TAG = "MyTag" + ProgressBarSet::class.java.simpleName
    private lateinit var progressBar: ProgressBar
    private lateinit var tv_progressBarInfo: TextView
    private lateinit var cancleButton: Button
    private lateinit var alertDialog: AlertDialog
    private lateinit var deviceConsoleActivity_initializeCallBack: DeviceConsoleActivity_initializeCallBack

    fun setSocketCloseCallback(callback: DeviceConsoleActivity_initializeCallBack) {
        Log.d(TAG, "setSocketCloseCallback() ")
        this.deviceConsoleActivity_initializeCallBack = callback
        Log.d(TAG, "setSocketCloseCallback: ${deviceConsoleActivity_initializeCallBack ?: "null"}")
    }
//    init {
//        alertDialogSet()
//    }

    fun alertDialogSet(){
        Log.d(TAG, "alertDialogSet()")
        var display = activity.windowManager.defaultDisplay
        val width = display.width
        val height = display.height

        var layoutInflater = LayoutInflater.from(context)
        val alertView = layoutInflater.inflate(R.layout.alertdialog_connecting, null)
        val builder = AlertDialog.Builder(context)
            .setCancelable(false)
        progressBar = alertView.findViewById<ProgressBar>(R.id.progressBar)
        tv_progressBarInfo = alertView.findViewById(R.id.tv_progressBarInfo)
        cancleButton = alertView.findViewById(R.id.button)
        cancleButton.setOnClickListener {
            Log.d(TAG, "alertDialogSet: AlertDialog Cancle")
            deviceConsoleActivity_initializeCallBack.stopConnectionAttempt()
            alertDialog.dismiss()
        }
        builder.setView(alertView)
        alertDialog = builder.create()
    }
    override fun changeProgressText(string: String){
        tv_progressBarInfo.text = string
    }
    fun showAlertDialog(){
        Log.d(TAG, "showAlertDialog()")
        alertDialog.show()
    }
    fun dissmissAlertDialog(){
        Log.d(TAG, "dissmissAlertDialog()")
        alertDialog.dismiss()
    }
}