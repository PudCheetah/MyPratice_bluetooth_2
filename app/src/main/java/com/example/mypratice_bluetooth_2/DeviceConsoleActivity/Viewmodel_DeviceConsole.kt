package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.app.Application
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mypratice_bluetooth_2.BroadcastManager_interface
import com.example.mypratice_bluetooth_2.Database.DataClass_BluetoothDeviceInfo
import com.example.mypratice_bluetooth_2.Database.DataClass_MessageInfo
import com.example.mypratice_bluetooth_2.Database.MessageDao
import com.example.mypratice_bluetooth_2.Database.MessageDatabase
import com.example.mypratice_bluetooth_2.MessageManager_interface
import com.example.mypratice_bluetooth_2.SocketManager_Interface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Viewmodel_DeviceConsole(application: Application): AndroidViewModel(application), MessageManager_interface, SocketManager_Interface, BroadcastManager_interface {
    private val TAG = "MyTagViewModel" + Viewmodel_DeviceConsole::class.java.simpleName
    var textMessageList = MutableLiveData<MutableList<DataClass_MessageInfo>>()

    var switchStatus = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()
    private var myDao: MessageDao? = null
    private var viewModelInitJob: Job

    init {
        textMessageList.value = mutableListOf()
        connectSocket.value = null
        switchStatus.value = null
        viewModelInitJob = CoroutineScope(Dispatchers.IO).launch {
            myDao = MessageDatabase?.getInstance(application)?.messageDao()
            textMessageList.postValue(myDao?.getAllMessage())
        }
    }


    fun addToDatabase(){
        CoroutineScope(Dispatchers.IO).launch {
            textMessageList.value?.forEach { messageInfo ->
                myDao?.UpsertMessage(messageInfo)
            }
        }
    }
    fun getViewmodelInitJob(): Job{
        return viewModelInitJob
    }

    override fun addToMessageList(address: String?,source: String?,time: String?,string: String
    ) {
        textMessageList.value?.add(DataClass_MessageInfo(null, address, source ?: "local", time ?: null,string))
        textMessageList.value = textMessageList.value
    }

    override fun gettextMessageList(): MutableList<DataClass_MessageInfo>? {
        return textMessageList.value
    }

    override suspend fun updateConnectSocket(socket: BluetoothSocket?) {
        withContext(Dispatchers.IO){
            connectSocket.postValue(socket)
        }
    }
    override fun updateSwitchStatus(isOn: Boolean) {
        switchStatus.value = isOn
    }
    override fun addDevice(device: DataClass_BluetoothDeviceInfo) {}
    override fun getScannedDevice(): MutableSet<DataClass_BluetoothDeviceInfo>? {
        return null
    }

    override fun clearConnectSocket() {
        connectSocket.value = null
    }


}