package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypratice_bluetooth_2.R
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding

class ProgressBarSet(val context: Context, val activity: AppCompatActivity) {
    private lateinit var progressBar: ProgressBar
    private lateinit var tv_progressBarInfo: TextView
    private lateinit var alertDialog: AlertDialog
    init {
        alertDialogSet()
    }
    fun alertDialogSet(){
        var display = activity.windowManager.defaultDisplay
        val width = display.width
        val height = display.height

        var layoutInflater = LayoutInflater.from(context)
        val alertView = layoutInflater.inflate(R.layout.alertdialog_connecting, null)
        val builder = AlertDialog.Builder(context).setCancelable(false)
        progressBar = alertView.findViewById<ProgressBar>(R.id.progressBar)
        tv_progressBarInfo = alertView.findViewById(R.id.tv_progressBarInfo)
        builder.setView(alertView)
        alertDialog = builder.create()
    }
    fun changeProgressInfo(string: String){
        tv_progressBarInfo.text = string
    }
    fun showAlertDialog(){
        alertDialog.show()
    }
    fun dissmissAlertDialog(){
        alertDialog.dismiss()
    }
}