package com.example.mypratice_bluetooth_2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataClass_BluetoothDeviceInfo(
    val deviceName: String ?= "unknow Name",
    val deviceAddress: String?= "unKnow Address",
    val deviceType: Int?= 404,
    val deviceUUID: String?= "unknow UUID"
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other){
            return true
        }else{
            if(this.javaClass != other?.javaClass){
                return false
            }else{
                other as DataClass_BluetoothDeviceInfo
                return (this.deviceAddress == other.deviceAddress)
            }
        }
    }

    override fun hashCode(): Int {
        return deviceAddress.hashCode()?: 0
    }
}
