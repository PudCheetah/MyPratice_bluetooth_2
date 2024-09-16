package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.app.Application
import android.bluetooth.BluetoothSocket
import android.location.Address
import android.util.Log
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
    var localAndrdoiID_VM = MutableLiveData<String>()
    var targetAndroidID_VM = MutableLiveData<String>()

    var switchStatus = MutableLiveData<Boolean>()
    var connectSocket = MutableLiveData<BluetoothSocket>()
    private var myDao: MessageDao? = null
    private var viewModelInitJob: Job
    var localAddress = MutableLiveData<String>()
    var targetAddress = MutableLiveData<String>()


    init {
        textMessageList.value = mutableListOf()
        localAndrdoiID_VM.value = ""
        targetAndroidID_VM.value = ""
        connectSocket.value = null
        switchStatus.value = null
        localAddress.value = ""
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

//    override fun addToMessageList(address: String?, sourceType: String?, time: String?, string: String) {
//        textMessageList.value?.add(DataClass_MessageInfo(null, address,getTargetAddress(), sourceType ?: "local", time ?: null,string, null, null))
//        textMessageList.value = textMessageList.value
//    }

    override fun gettextMessageList(): MutableList<DataClass_MessageInfo>? {
        return textMessageList.value
    }

    override fun getLocalAddress(): String? {
        return localAddress.value
    }

    override fun getTargetAddressFromConnectSocket(): String? {
        return connectSocket.value?.remoteDevice?.address
    }

    //已讀功能，根據randomMessageID尋找textMessageList對應的資料並將他的reciveStatus改為true
    override fun findAndUpdate_textMessageList(randomMessageID: String?) {
        val filterString = randomMessageID
        Log.d(TAG, "findAndUpdate_textMessageListA: ${randomMessageID}")
        Log.d(TAG, "findAndUpdate_textMessageListB: ${textMessageList.value?.last()?.randomID}")
        textMessageList.value?.replaceAll { if (it.randomID == filterString) it.copy(reciveStatus = true)else it }
        textMessageList.value = textMessageList.value
        Log.d(TAG, "findAndUpdate_textMessageList: ${textMessageList.value}")
    }
    //將訊息根據夾帶的sourceAndroidID是否和本機相等來加上local，並將其放入textMessageList
    override fun updateVM_textMessageList(randomMessageID: String,sourceAndroidID: String?, targetAndroidID: String?,message: String) {
        Log.d(TAG, "updateVM_textMessageList: ${localAndrdoiID_VM.value}")
        var sourceType = ""
        if(sourceAndroidID == localAndrdoiID_VM.value){
            sourceType = "local"
        }else{
            sourceType = "other"
        }
        textMessageList.value?.add(DataClass_MessageInfo(null, sourceAndroidID, targetAndroidID, sourceType,null, message, false, randomMessageID))
        textMessageList.value = textMessageList.value
        Log.d(TAG, "updateVM_textMessageList: ${textMessageList.value}")
    }


    override suspend fun updateConnectSocket(socket: BluetoothSocket?) {
        withContext(Dispatchers.IO){
            connectSocket.postValue(socket)
        }
    }

    override suspend fun updateLocalAndrdoiID(localAndroidID: String) {
        withContext(Dispatchers.Main){
            Log.d(TAG, "updateLocalAndrdoiID: ${localAndroidID}")
            localAndrdoiID_VM.value = localAndroidID
        }
    }
    override suspend fun updateTargetAndroidID(targetAndroidID: String) {
        targetAndroidID_VM.value = targetAndroidID
    }
    override suspend fun getLocalAndrdoiID(): String? {
        return localAndrdoiID_VM.value
    }

    override suspend fun getTargetAndroidID(): String? {
        return targetAndroidID_VM.value
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