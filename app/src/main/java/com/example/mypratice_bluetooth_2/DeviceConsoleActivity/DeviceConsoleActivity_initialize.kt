package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.DeviceConsoleActivity_initializeCallBack
import com.example.mypratice_bluetooth_2.MessageManager
import com.example.mypratice_bluetooth_2.SocketManager_client
import com.example.mypratice_bluetooth_2.SocketManager_server
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class DeviceConsoleActivity_initialize(
    private val activity: DeviceConsoleActivity,
    private val binding: ActivityDeviceConsoleBinding,
    private val bluetoothAction: BluetoothAction,
    private val socketManagerServer: SocketManager_server,
    private val socketmanagerClient: SocketManager_client,
    private val bluetoothDevice: BluetoothDevice,
    private val viewModel: Viewmodel_DeviceConsole,
    private val messageManager: MessageManager,
    private val bluetoothAdapter: BluetoothAdapter,
    private val progressBarSet: ProgressBarSet
): DeviceConsoleActivity_initializeCallBack {
    private val TAG = "MyTag" + DeviceConsoleActivity_initialize::class.java.simpleName
    private lateinit var initializeConnect_Job: Job
    private var stopRequested = false

    init {
        initializeUI()
//        progressBarSet.showAlertDialog()
        initializeConnect()
    }
    //初始化連線
    private fun initializeConnect() {
        initializeConnect_Job = CoroutineScope(Dispatchers.IO).launch {
            stopRequested = false
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 45000 && !(viewModel.connectSocket.value?.isConnected ?: false)){
                val innerLoopStartTime = System.currentTimeMillis()
                val randomTimeList = listOf(0, 2000, 5000)
                var randomTime = randomTimeList.random()
                if(stopRequested){
                    Log.d(TAG, "Connection attempt stopped by user.")
                    return@launch // 退出協程
                }
                //會在3秒內不斷嘗試"createBluetoothClientSocket_2"直到連線成功或10秒
                while (System.currentTimeMillis() - innerLoopStartTime < (2000 + randomTime) && !(viewModel.connectSocket.value?.isConnected ?: false)) {
                    if(stopRequested){
                        Log.d(TAG, "Connection attempt stopped by user.")
                        return@launch // 退出協程
                    }
                    if (socketmanagerClient.createBluetoothClientSocket_2(bluetoothDevice) == true) {
                        messageManager.sendAuthenticationMessage(viewModel.connectSocket.value)
                        messageManager.receiveAuthenticationMessage(viewModel.connectSocket.value)
                        Log.d(TAG, "initializeConnect: createBluetoothClientSocket_2")
                        break
                    } else {
                        delay(1000) // 等待1秒後再次嘗試
                    }
                }
                //若嘗試客戶端連線失敗，則建立伺服器端
                if (!(viewModel.connectSocket.value?.isConnected ?: false)) {
                    //生成一個計時的線呈來關閉socket
                    CoroutineScope(Dispatchers.IO).launch{
                        delay(4000)
                        socketManagerServer.stopSocket()
                    }
                    //生成伺服器端
                    if(socketManagerServer.createBluetoothServerSocket_2(bluetoothAdapter) == true){
                        messageManager.receiveAuthenticationMessage(viewModel.connectSocket.value)
                        messageManager.sendAuthenticationMessage(viewModel.connectSocket.value)
                        Log.d(TAG, "initializeConnect: createBluetoothServerSocket_2")
                        break
                    }
                }
            }
            if(viewModel.connectSocket.value?.isConnected ?: false){
                Log.d(TAG, "localIDA: ${viewModel.localAndrdoiID_VM.value}, targetID: ${viewModel.targetAndroidID_VM.value}")
                messageManager.receiveMessages(viewModel.connectSocket.value)
                progressBarSet.dissmissAlertDialog()
            }else{
                progressBarSet.dissmissAlertDialog()
                withContext(Dispatchers.Main){
                    Toast.makeText(activity, "連線逾時，請嘗試手動連線", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    //停止initializeConnect
    override fun stopConnectionAttempt() {
        Log.d(TAG, "stopConnectionAttempt()")
        socketManagerServer.stopSocket()
        progressBarSet.dissmissAlertDialog()
        stopRequested = true
        initializeConnect_Job?.cancel() // 停止協程
    }


    //初始化UI
    fun initializeUI(){
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Problem")
        }
        binding.tvDeviceName.text = bluetoothDevice?.name
        binding.tvDeviceAddress.text = bluetoothDevice?.address
        binding.tvDeviceType.text = bluetoothDevice?.type.toString()
        binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        checkSwitchStatus()
        CoroutineScope(Dispatchers.Main).launch {
            joinAll(viewModel.getViewmodelInitJob())
            setupFragment()
            listenterAndObserve_set()
        }
    }

    private fun listenterAndObserve_set(){
        setupButton()
        setupSwitch()
        setupObserve()
//        setupRV()
    }

    private fun setupButton(){
        binding.btnSendMessage.setOnClickListener {
            btnAction_sendMessage()
        }
        binding.btnConnect.setOnClickListener {
            btnAction_reconnect()
        }
    }
    private fun setupSwitch(){
        binding.switch1.setOnCheckedChangeListener { buttonView, isCheck ->
            if(isCheck){
                bluetoothAction.enableBluetooth()
            }else{
                bluetoothAction.disableBluetooth()
            }
        }
    }
    private fun setupObserve(){
        viewModel.textMessageList.observe(activity){
            if(viewModel.textMessageList.value?.size != 0){
                Toast.makeText(activity, "${viewModel.textMessageList.value?.last()}", Toast.LENGTH_SHORT).show()
            }
            CoroutineScope(Dispatchers.Main).launch{
//                binding.rvDeviceConsole.adapter?.notifyDataSetChanged()
//                binding.rvDeviceConsole.scrollToPosition((viewModel.textMessageList.value!!.size - 1))
                binding.root.invalidate()
                viewModel.addLastMessageToDatabase()
            }
        }
        viewModel.switchStatus.observe(activity){
            checkSwitchStatus()
        }
    }
    private fun setupFragment(){
        val fragmentTransation = activity.supportFragmentManager.beginTransaction()
        fragmentTransation.replace(binding.fragmentContainerView.id, MessageFragment.instance)
        fragmentTransation.commit()
    }
//    private fun setupRV(){
//        binding.rvDeviceConsole.layoutManager = LinearLayoutManager(activity)
//        binding.rvDeviceConsole.adapter = RvAdapter_MessageFragment(viewModel)
//    }


    private fun btnAction_sendMessage(){
        if(bluetoothAdapter.isEnabled){
            if(viewModel.connectSocket.value?.isConnected ?: false){
                if(binding.etMessageInput.text?.isEmpty() == true){
                    Toast.makeText(activity, "不可傳送空白訊息", Toast.LENGTH_SHORT).show()
                }else{
                    val message = binding.etMessageInput.text.toString()
                    messageManager.sendMessage(viewModel.connectSocket.value, message, true)
                    binding.etMessageInput.text?.clear()
                }
            }else{
                Toast.makeText(activity, "連線尚未建立", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "請先開啟藍芽", Toast.LENGTH_SHORT).show()
        }
    }
    private fun btnAction_reconnect(){
        viewModel.connectSocket.value?.close()
        viewModel.connectSocket.value = null
//        progressBarSet.showAlertDialog()
        initializeConnect()
        progressBarSet.alertDialogSet()
        progressBarSet.showAlertDialog()
    }
    private fun checkSwitchStatus(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }


}