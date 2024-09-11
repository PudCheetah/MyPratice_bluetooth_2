package com.example.mypratice_bluetooth_2.MainActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypratice_bluetooth_2.BluetoothAction
import com.example.mypratice_bluetooth_2.IntentLauncher
import com.example.mypratice_bluetooth_2.databinding.ActivityMainBinding

class MainActivity_setupUI(
    private val activity: MainActivity,
    private val viewModel: ViewModel_MainActivity,
    private val binding: ActivityMainBinding,
    private val bluetoothAction: BluetoothAction,
    private val intentLauncher: IntentLauncher) {

    fun setupListenersAndObservers(){
        setupSwitch()
        setupObserve()
        setupButton()
        setupRV()
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
    private fun setupButton(){
        binding.floatingActionButton3.setOnClickListener {
            bluetoothAction.activityScanning()
            bluetoothAction.changeBluetoothMode_discoverable()
        }
    }
    private fun setupObserve(){
        viewModel.scannedDevices.observe(activity){
            binding.rvMainActivity.adapter?.notifyDataSetChanged()
            binding.root.invalidate()
        }
        viewModel.switchStatu.observe(activity){
            activity.checkSwitchStatus()
        }
    }
    private fun setupRV(){
        binding.rvMainActivity.layoutManager = LinearLayoutManager(activity)
        binding.rvMainActivity.adapter = RvAdapter_mainActivity(activity, viewModel, intentLauncher)
    }
}