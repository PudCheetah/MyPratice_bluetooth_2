package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.MessageManager
import com.example.mypratice_bluetooth_2.SocketManager_client
import com.example.mypratice_bluetooth_2.SocketManager_server
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class DeviceConsoleActivity_initializeUI(
    private val activity: DeviceConsoleActivity,
    private val binding: ActivityDeviceConsoleBinding,
    private val bluetoothAction: BluetoothAction,
    private val bluetoothDevice: BluetoothDevice,
    private val viewModel: Viewmodel_DeviceConsole,
    private val messageManager: MessageManager,
    private val bluetoothAdapter: BluetoothAdapter,
    private val progressBarSet: ProgressBarSet,
    private val deviceConsoleActivityInitializeConnect: DeviceConsoleActivity_InitializeConnect
){
    private val TAG = "MyTag" + DeviceConsoleActivity_initializeUI::class.java.simpleName

    //初始化UI
    fun initialize_UI(){
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

    //監聽器及觀察者整合
    private fun listenterAndObserve_set(){
        setupButton()
        setupSwitch()
        setupObserve()
//        setupRV()
    }

    //按鈕監聽器整合
    private fun setupButton(){
        binding.btnSendMessage.setOnClickListener {
            btnAction_sendMessage()
        }
        binding.btnConnect.setOnClickListener {
            btnAction_reconnect()
        }
    }
    //switch監聽器整合
    private fun setupSwitch(){
        binding.switch1.setOnCheckedChangeListener { buttonView, isCheck ->
            if(isCheck){
                bluetoothAction.enableBluetooth()
            }else{
                bluetoothAction.disableBluetooth()
            }
        }
    }
    //觀察者整合
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


    //按鈕功能
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
    //按鈕功能
    private fun btnAction_reconnect(){
        viewModel.connectSocket.value?.close()
        viewModel.connectSocket.value = null
        deviceConsoleActivityInitializeConnect.connectionAttempt()
        progressBarSet.alertDialogSet()
        progressBarSet.showAlertDialog()
    }

    private fun checkSwitchStatus(){
        binding.switch1.isChecked = bluetoothAdapter.isEnabled
    }


}