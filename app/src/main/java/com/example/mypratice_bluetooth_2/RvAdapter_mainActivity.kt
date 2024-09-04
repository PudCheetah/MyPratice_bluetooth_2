package com.example.mypratice_bluetooth_2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mypratice_bluetooth_2.databinding.RvItemMainActivityBinding

class RvAdapter_mainActivity(val context: Context, val viewModel: ViewModel_MainActivity, val intentLauncher: IntentLauncher): RecyclerView.Adapter<RvAdapter_mainActivity.MyRvHolder>() {
    inner class MyRvHolder(itemView: RvItemMainActivityBinding): RecyclerView.ViewHolder(itemView.root){
        val tvDeviceName = itemView.tvDeviceName
        val tvDeviceAddress = itemView.tvDeviceAddress
        val tvDeviceType = itemView.tvDeviceType
        val tvDeviceUUID = itemView.tvDeviceUUID
        val rv_item_constraintLayout = itemView.rvItemConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRvHolder {
        return MyRvHolder(RvItemMainActivityBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return viewModel.scannedDevices.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyRvHolder, position: Int) {
        var deviceList = viewModel.scannedDevices.value?.toList()
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        with(holder){
            tvDeviceName.text = deviceList?.get(position)?.deviceName ?: "unknow deviceName"
            tvDeviceAddress.text = deviceList?.get(position)?.deviceAddress ?: "unknow deviceAddress"
            tvDeviceType.text = deviceList?.get(position)?.deviceType?.toString() ?: "unknow deviceType"
            tvDeviceUUID.text = deviceList?.get(position)?.deviceUUID ?: "unknow deviceUUID"
            rv_item_constraintLayout.setOnClickListener {
                val deviceInfo = DataClass_BluetoothDeviceInfo(
                    deviceList?.get(position)?.deviceName ?: "unknow deviceName",
                    deviceList?.get(position)?.deviceAddress ?: "unknow deviceAddress",
                    deviceList?.get(position)?.deviceType ?: 0,
                    deviceList?.get(position)?.deviceUUID ?: "unknow deviceUUID"
                )
                val intent = Intent(context, DeviceConsole::class.java)
                intent.putExtra("DeviceInfo", deviceInfo)
                intentLauncher.activityIntent(intent)
            }
        }
    }
}