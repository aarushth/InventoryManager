package com.leopardseal.inventorymanagerapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leopardseal.inventorymanagerapp.databinding.FragmentLoginBinding
import com.leopardseal.inventorymanagerapp.network.LoginAPI
import com.leopardseal.inventorymanagerapp.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, LoginRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }


    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getRepository() = LoginRepository(serverComms.buildApi(LoginAPI::class.java))

}