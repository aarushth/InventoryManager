package com.leopardseal.inventorymanagerapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.leopardseal.inventorymanagerapp.databinding.FragmentLoginBinding
import com.leopardseal.inventorymanagerapp.network.LoginAPI
import com.leopardseal.inventorymanagerapp.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import kotlinx.coroutines.runBlocking


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, LoginRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer{
            when(it){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "logged in as" + it.myUser.email, Toast.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "user not found in system" , Toast.LENGTH_LONG).show()
                }
            }
        })


        binding.googleSignInBtn.setOnClickListener{
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                 .setFilterByAuthorizedAccounts(false)
                 .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
                 .setAutoSelectEnabled(false)
                 .build()
            val ctext : Context = requireContext()
            val credentialManager = CredentialManager.create(ctext)
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                 .addCredentialOption(googleIdOption)
                 .build()
 
            runBlocking {
 
                 try {
                     val result = credentialManager.getCredential(
                         request = request,
                         context = ctext,
                     )
                     serverComms.setToken(GoogleIdTokenCredential.createFrom(result.credential.data))
                     viewModel.login()
                 } catch (e: Throwable) {
                     Log.e(TAG, "something failed")
                 }
            }
        }
    }


    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getRepository() = LoginRepository(serverComms.buildApi(LoginAPI::class.java))

}