package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.app.Application
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypratice_bluetooth_2.Database.DataClass_MessageInfo
import com.example.mypratice_bluetooth_2.Database.MessageDao
import com.example.mypratice_bluetooth_2.Database.MessageDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Viewmodel_DeviceConsole(application: Application): AndroidViewModel(application) {
    private val TAG = "MyTagViewModel" + Viewmodel_DeviceConsole::class.java
    var textMessageList = MutableLiveData<MutableList<DataClass_MessageInfo>>()

    var isClient = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()
    private var myDao: MessageDao? = null
    private lateinit var viewModelInitJob: Job

    init {
        textMessageList.value = mutableListOf()
        isClient.value = true
        connectSocket.value = null
        viewModelInitJob = CoroutineScope(Dispatchers.IO).launch {
            myDao = MessageDatabase?.getInstance(application)?.messageDao()
            textMessageList.postValue(myDao?.getAllMessage())
        }
    }
    fun addToMessageList(address: String?, source: String?, time: String?, string: String){
        textMessageList.value?.add(DataClass_MessageInfo(null, address, source ?: "local", time ?: null,string))
        textMessageList.value = textMessageList.value
    }

    fun addToDatabase(){
        CoroutineScope(Dispatchers.IO).launch {
            textMessageList.value?.forEach { messageInfo ->
                myDao?.UpsertMessage(messageInfo)
            }
        }
    }

}