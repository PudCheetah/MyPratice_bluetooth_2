package com.example.mypratice_bluetooth_2.DeviceConsoleActivity

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mypratice_bluetooth_2.databinding.RvItemDeviceConsoleBinding

class RvAdapter_MessageFragment(var viewModel: Viewmodel_DeviceConsole): RecyclerView.Adapter<RvAdapter_MessageFragment.MyViewHolder>(){
    inner class MyViewHolder(itemView: RvItemDeviceConsoleBinding): RecyclerView.ViewHolder(itemView.root){
        val tv_1_rvItem = itemView.tv1RvItem
        val cv_1_rvitem= itemView.cv1Rvitem
        val tv_source = itemView.tvSource
        val tv_reply = itemView.TVReply
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
            if(messageInfo?.sourceType == "local"){
                //變更顏色
                tv_1_rvItem.setBackgroundColor(Color.GREEN)
                // 置右
                CL_set(cv_1_rvitem, (0.95).toFloat())
                CL_set(tv_source, (1).toFloat())
                CL_set(tv_reply, (0.95).toFloat())
            }else{
                //變更顏色
                tv_1_rvItem.setBackgroundColor(Color.GRAY)
                // 置左
                CL_set(cv_1_rvitem, (0.05).toFloat())
                CL_set(tv_source, (0).toFloat())
                CL_set(tv_reply, (0).toFloat())
            }
            tv_1_rvItem.text = messageInfo?.message
            tv_source.text = messageInfo?.sourceAndroidID

            //改進版: 如果sourceType不為local，就隱藏，不為local且reciveStatus為true則顯示成功，false顯示失敗
            tv_reply.isInvisible = messageInfo?.sourceType != "local"
            if (!tv_reply.isInvisible) {
                tv_reply.text = if (messageInfo?.reciveStatus == true) "傳送成功" else "傳送失敗"
            }
            //原版: 如果sourceType不為local，就隱藏，不為local且reciveStatus為true則顯示成功，false顯示失敗
//            if (messageInfo?.sourceType != "local"){
//                tv_reply.isInvisible = true
//            }else{
//                tv_reply.isInvisible = false
//                if(messageInfo?.reciveStatus.toString() == "true"){
//                    tv_reply.text = "傳送成功"
//                }else{
//                    tv_reply.text = "傳送失敗"
//                }
//            }
        }

    }
    //按照percent(0-1)的比例來變更view的水平位置
    fun CL_set(view: View, percent: Float){
        (view.layoutParams as ConstraintLayout.LayoutParams).apply {
            horizontalBias = percent
        }
        view.requestLayout()
    }
}