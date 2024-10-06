package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.widget.Toast
import com.example.mypratice_bluetooth_2.MessageManager
import com.example.mypratice_bluetooth_2.SocketManager_client
import com.example.mypratice_bluetooth_2.SocketManager_server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceConsoleActivity_InitializeConnect(
    private val activity: DeviceConsoleActivity,
    private val socketManagerServer: SocketManager_server,
    private val socketmanagerClient: SocketManager_client,
    private val bluetoothDevice: BluetoothDevice,
    private val viewModel: Viewmodel_DeviceConsole,
    private val messageManager: MessageManager,
    private val bluetoothAdapter: BluetoothAdapter,
    private val progressBarSet: ProgressBarSet
) : DeviceConsoleActivity_initializeConnect_CallBack {
    private val TAG = "MyTag" + DeviceConsoleActivity_InitializeConnect::class.java.simpleName
    private lateinit var initializeConnect_Job: Job
    private var stopRequested = false


    //嘗試連線
    fun connectionAttempt() {
        initializeConnect_Job = CoroutineScope(Dispatchers.IO).launch {
            stopRequested = false
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 45000 && !(viewModel.connectSocket.value?.isConnected
                    ?: false)
            ) {
                val innerLoopStartTime = System.currentTimeMillis()
                val randomTimeList = listOf(0, 2000, 5000)
                var randomTime = randomTimeList.random()
                if (stopRequested) {
                    Log.d(TAG, "Connection attempt stopped by user.")
                    return@launch // 退出協程
                }
                //會在3秒內不斷嘗試"createBluetoothClientSocket_2"直到連線成功或10秒
                while (System.currentTimeMillis() - innerLoopStartTime < (2000 + randomTime) && !(viewModel.connectSocket.value?.isConnected
                        ?: false)
                ) {
                    if (stopRequested) {
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
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(4000)
                        socketManagerServer.stopSocket()
                    }
                    //生成伺服器端
                    if (socketManagerServer.createBluetoothServerSocket_2(bluetoothAdapter) == true) {
                        messageManager.receiveAuthenticationMessage(viewModel.connectSocket.value)
                        messageManager.sendAuthenticationMessage(viewModel.connectSocket.value)
                        Log.d(TAG, "initializeConnect: createBluetoothServerSocket_2")
                        break
                    }
                }
            }
            //挑出迴圈後，檢查socket是否生成，有的話則啟動訊息監聽
            if (viewModel.connectSocket.value?.isConnected ?: false) {
                Log.d(
                    TAG,
                    "localIDA: ${viewModel.localAndrdoiID_VM.value}, targetID: ${viewModel.targetAndroidID_VM.value}"
                )
                messageManager.receiveMessages(viewModel.connectSocket.value)
                progressBarSet.dissmissAlertDialog()
            } else {
                progressBarSet.dissmissAlertDialog()
                withContext(Dispatchers.Main) {
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
}