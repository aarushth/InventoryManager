package com.leopardseal.inventorymanagerapp.ui.login

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest

import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.Observer

import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus

import com.leopardseal.inventorymanagerapp.databinding.FragmentLoginBinding
import com.leopardseal.inventorymanagerapp.data.network.LoginAPI
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.responses.MyUsers
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment
import com.leopardseal.inventorymanagerapp.ui.home.HomeActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import com.leopardseal.inventorymanagerapp.ui.visible
import kotlinx.coroutines.runBlocking


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, LoginRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visible(false)
//        binding.googleSignInBtn.enabled(false)

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(false)
            when (it) {
                is Resource.Success<MyUsers> -> {
                    Toast.makeText(requireContext(), it.value.email, Toast.LENGTH_LONG).show()
                }

                is Resource.Failure -> {
                    if(it.isNetworkError) {
                        Toast.makeText(requireContext(),"please check your internet and try again",Toast.LENGTH_LONG).show()
                    }else if(it.errorCode == HttpStatus.SC_UNAUTHORIZED){
                        Toast.makeText(requireContext(),"user not found in system",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(requireContext(),"an error occured, please try again later",Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        triggerLogin()
        binding.googleSignInBtn.setOnClickListener{
            triggerLogin()
        }

    }
    fun triggerLogin(){
        binding.progressBar.visible(true)
        val context: Context = requireContext()
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
//            .setAutoSelectEnabled(false)
            .build()

        val credentialManager = CredentialManager.create(context)
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        runBlocking {

            try {
                login(context, credentialManager, request)
            } catch (e: GetCredentialException) {
                if (e is NoCredentialException) {
                    try {
                        val googleIdOption2: GetGoogleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
                            .setAutoSelectEnabled(false)
                            .build()

                        val request2: GetCredentialRequest = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption2)
                            .build()
                        login(context, credentialManager, request2)
                    } catch (e: GetCredentialException) {
                        Log.e(TAG, e.toString())
                    }
                } else {
                    Log.e(TAG, e.toString())
                }

            }
        }
    }
    suspend fun login(context: Context, credentialManager: CredentialManager, request: GetCredentialRequest){
        val result = credentialManager.getCredential(
            request = request,
            context = context,
        )
        if(result.credential is CustomCredential && result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            val token : String = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
            viewModel.saveAuthToken(token)
            viewModel.login(token)
            requireActivity().startNewActivity(HomeActivity::class.java)
        }
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getRepository() = LoginRepository(serverComms.buildApi(LoginAPI::class.java), userPreferences)

}