package com.example.mypratice_bluetooth_2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mypratice_bluetooth_2.databinding.RvItemDeviceConsoleBinding
import com.google.android.material.color.MaterialColors.getColor

class RvAdapter_deviceConsole(var viewModel: Viewmodel_DeviceConsole): RecyclerView.Adapter<RvAdapter_deviceConsole.MyViewHolder>(){
    inner class MyViewHolder(itemView: RvItemDeviceConsoleBinding): RecyclerView.ViewHolder(itemView.root){
        val tv_1_rvItem = itemView.tv1RvItem
        val cv_1_rvitem= itemView.cv1Rvitem
        val CSL_1_rvitem = itemView.CSL1Rvitem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RvItemDeviceConsoleBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return viewModel.textMessageList.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        with(holder){
            val messageInfo = viewModel.textMessageList.value?.get(position)
            if(messageInfo?.source == "local"){
                //變更顏色
                tv_1_rvItem.setBackgroundColor(Color.GREEN)
                // 置右
                val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
                constraintSet.clone(CSL_1_rvitem)
                constraintSet.clear(cv_1_rvitem.id, androidx.constraintlayout.widget.ConstraintSet.START) // 清除 START 約束
                constraintSet.connect(cv_1_rvitem.id, androidx.constraintlayout.widget.ConstraintSet.END, androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.END, 16)
                constraintSet.applyTo(CSL_1_rvitem)

            }else{
                //變更顏色
                tv_1_rvItem.setBackgroundColor(Color.GRAY)
                // 置左
                val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
                constraintSet.clone(CSL_1_rvitem)
                constraintSet.clear(cv_1_rvitem.id, androidx.constraintlayout.widget.ConstraintSet.END) // 清除 END 約束
                constraintSet.connect(cv_1_rvitem.id, androidx.constraintlayout.widget.ConstraintSet.START, androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.START, 16)
                constraintSet.applyTo(CSL_1_rvitem)
            }
            tv_1_rvItem.text = messageInfo?.message
        }
    }
}