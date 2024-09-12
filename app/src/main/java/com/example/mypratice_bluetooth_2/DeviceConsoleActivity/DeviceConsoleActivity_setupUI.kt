package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.MessageManager
import com.example.mypratice_bluetooth_2.SocketManager
import com.example.mypratice_bluetooth_2.databinding.ActivityDeviceConsoleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceConsoleActivity_setupUI(
    private val activity: DeviceConsoleActivity,
    private val binding: ActivityDeviceConsoleBinding,
    private val bluetoothAction: BluetoothAction,
    private val socketManager: SocketManager,
    private val bluetoothDevice: BluetoothDevice,
    private val viewModel: Viewmodel_DeviceConsole,
    private val messageManager: MessageManager,
    private val bluetoothAdapter: BluetoothAdapter
) {
    private val TAG = "MyTag" + DeviceConsoleActivity_setupUI::class.java.simpleName


    fun initializeUI(){
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Problem")
        }
        binding.tvDeviceName.text = bluetoothDevice?.name
        binding.tvDeviceAddress.text = bluetoothDevice?.address
        binding.tvDeviceType.text = bluetoothDevice?.type.toString()
        binding.tvDeviceUUID.text = bluetoothDevice?.uuids.toString()
        activity.checkSwitchStatus()
        CoroutineScope(Dispatchers.Main).launch {
            joinAll(viewModel.getViewmodelInitJob())
            listenterAndObserve_set()
        }
    }

    private fun listenterAndObserve_set(){
        setupButton()
        setupSwitch()
        setupObserve()
        setupRV()
    }

    private fun setupButton(){
        binding.btnConnect.setOnClickListener {
            btnAction_connect()
        }
        binding.btnSendMessage.setOnClickListener {
            btnAction_sendMessage()
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
//        viewModel.textMessageList.observe(activity){
//            if(viewModel.textMessageList.value?.size != 0){
//                Toast.makeText(activity, "${viewModel.textMessageList.value?.last()}", Toast.LENGTH_SHORT).show()
//            }
//            binding.rvDeviceConsole.adapter?.notifyDataSetChanged()
//            CoroutineScope(Dispatchers.Main).launch{
//                binding.rvDeviceConsole.scrollToPosition((viewModel.textMessageList.value!!.size - 1))
//            }
//            binding.root.invalidate()
//        }
        viewModel.switchStatus.observe(activity){
            activity.checkSwitchStatus()
        }
    }
    private fun setupRV(){
        binding.rvDeviceConsole.layoutManager = LinearLayoutManager(activity)
        binding.rvDeviceConsole.adapter = RvAdapter_deviceConsole(viewModel)
    }

    private fun btnAction_connect(){
        CoroutineScope(Dispatchers.IO).launch {
            if(socketManager.createBluetoothClientSocket_2(bluetoothDevice) == true){

            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(activity, "嘗試建立伺服器", Toast.LENGTH_SHORT).show()
                }
                socketManager.createBluetoothServerSocket_2(bluetoothAdapter)
            }
            messageManager.receiveMessages(viewModel.connectSocket.value)
        }
    }
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
}