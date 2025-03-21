package com.leopardseal.inventorymanagerapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.leopardseal.inventorymanagerapp.data.network.ServerComms
import com.leopardseal.inventorymanagerapp.data.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class BaseFragment<VM : ViewModel, B: ViewBinding, R: BaseRepository> : Fragment() {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected val serverComms = ServerComms()
    protected lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch { userPreferences.authToken.first() }
        binding = getFragmentBinding(inflater, container)
        val factory = VMFactory(getRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        return binding.root
    }

    abstract fun getViewModel(): Class<VM>
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?):B
    abstract fun getRepository():R

}