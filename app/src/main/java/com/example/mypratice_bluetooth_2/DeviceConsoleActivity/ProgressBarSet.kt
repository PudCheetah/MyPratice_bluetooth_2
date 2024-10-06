package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypratice_bluetooth_2.R

class ProgressBarSet(val context: Context, val activity: AppCompatActivity): ProgressBarSet_interface {
    private val TAG = "MyTag" + ProgressBarSet::class.java.simpleName
    private lateinit var progressBar: ProgressBar
    private lateinit var tv_progressBarInfo: TextView
    private lateinit var cancleButton: Button
    private lateinit var alertDialog: AlertDialog
    private lateinit var initializeConnectCallBack: DeviceConsoleActivity_initializeConnect_CallBack

    fun setSocketCloseCallback(callback: DeviceConsoleActivity_initializeConnect_CallBack) {
        Log.d(TAG, "setSocketCloseCallback() ")
        this.initializeConnectCallBack = callback
        Log.d(TAG, "setSocketCloseCallback: ${initializeConnectCallBack ?: "null"}")
    }

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
            initializeConnectCallBack.stopConnectionAttempt()
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