package com.leopardseal.inventorymanagerapp.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest

import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.databinding.FragmentLoginBinding

import com.leopardseal.inventorymanagerapp.data.network.Resource

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.leopardseal.inventorymanagerapp.data.responses.dto.LoginResponse
import com.leopardseal.inventorymanagerapp.ui.enabled
import com.leopardseal.inventorymanagerapp.ui.main.MainActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import com.leopardseal.inventorymanagerapp.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        navController = view.findNavController()
        binding.progressBar.visible(false)
//        getActionBar()?.setTitle("Login");
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(false)
            when (it) {

                is Resource.Success<LoginResponse> -> {
//                    viewModel.saveUserId(it.value.id)
                    if(it.value.user.picture != null) {
                        viewModel.savePictureUrl(it.value.user.picture!!)
                    }
                    viewModel.saveToken(it.value.token)
                    
                }

                is Resource.Failure -> {
                    if(it.isNetworkError) {
                        binding.googleSignInBtn.enabled(true)
                        Toast.makeText(requireContext(),"please check your internet and try again",Toast.LENGTH_LONG).show()
                    }else if(it.errorCode == HttpStatus.SC_UNAUTHORIZED){
//                        Toast.makeText(requireContext(),"user not found in system",Toast.LENGTH_LONG).show()
                        navController.navigate(R.id.login_fail)
                    }else{
                        binding.googleSignInBtn.enabled(true)
                        Toast.makeText(requireContext(),"an error occured, please try again later",Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading, Resource.Init -> TODO()

            }
        })

        binding.googleSignInBtn.setOnClickListener{
            triggerLogin()
        }
        triggerLogin()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tokenSaved.collect { saved ->
                if (saved) {
                    viewModel.resetTokenSavedFlag()
                    requireActivity().startNewActivity(MainActivity::class.java)
                    
                }
            }
        }
    }




    fun triggerLogin(){
        binding.progressBar.visible(true)
        binding.googleSignInBtn.enabled(false)
        val context: Context = requireContext()
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
            .build()

        val credentialManager = CredentialManager.create(context)
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        runBlocking {

            try {
                login(context, credentialManager, request)
            } catch (e: GetCredentialException) {
//                if (e is NoCredentialException) {
//                    try {
//                        val googleIdOption2: GetGoogleIdOption = GetGoogleIdOption.Builder()
//                            .setFilterByAuthorizedAccounts(false)
//                            .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
//                            .setAutoSelectEnabled(false)
//                            .build()
//
//                        val request2: GetCredentialRequest = GetCredentialRequest.Builder()
//                            .addCredentialOption(googleIdOption2)
//                            .build()
//                        login(context, credentialManager, request2)
//                    } catch (e: GetCredentialException) {
//                        Log.e(TAG, e.toString())
//                    }
//                } else {
//                    Log.e(TAG, e.toString())
//                }
            }
        }
        binding.googleSignInBtn.enabled(true)
    }
    @SuppressLint("CredentialManagerMisuse")
    suspend fun login(context: Context, credentialManager: CredentialManager, request: GetCredentialRequest){
        val result = credentialManager.getCredential(
            request = request,
            context = context,
        )
        if(result.credential is CustomCredential && result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            val token : String = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
            viewModel.login(token)
        }
    }

//    override fun getViewModel() = LoginViewModel::class.java
//
//    override fun getFragmentBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?
//    ) = FragmentLoginBinding.inflate(inflater, container, false)
//
//    override fun getRepository() = LoginRepository(serverComms.buildApi(LoginAPI::class.java), userPreferences)

}