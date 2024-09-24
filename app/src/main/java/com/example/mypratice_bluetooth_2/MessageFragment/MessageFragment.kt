package com.example.mypratice_bluetooth_2.MessageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypratice_bluetooth_2.DeviceConsoleActivity.Viewmodel_DeviceConsole
import com.example.mypratice_bluetooth_2.databinding.FragmentMessageBinding

class MessageFragment() : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private lateinit var viewModel: Viewmodel_DeviceConsole

    companion object{
        val instance: MessageFragment by lazy {
            MessageFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(Viewmodel_DeviceConsole::class.java)
        setupRV()
        viewModel.textMessageList.observe(viewLifecycleOwner){
            binding.rvMessageFragment.adapter?.notifyDataSetChanged()
            binding.rvMessageFragment.scrollToPosition((viewModel.textMessageList.value!!.size - 1))
            binding.root.invalidate()
        }
        return binding.root
    }
    private fun setupRV(){
        binding.rvMessageFragment.layoutManager = LinearLayoutManager(activity)
        binding.rvMessageFragment.adapter = RvAdapter_MessageFragment(viewModel)
    }

}