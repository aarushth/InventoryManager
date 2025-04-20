package com.leopardseal.inventorymanagerapp.ui.main.invite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.API.InviteAPI
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.InviteRepository
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import com.leopardseal.inventorymanagerapp.databinding.FragmentInviteBinding
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class InviteFragment : BaseFragment<InviteViewModel, FragmentInviteBinding, InviteRepository>() {

    private lateinit var inviteListAdapter : InviteListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getActionBar()?.setTitle("Select an Organization")
        var v : View? = super.onCreateView(inflater, container, savedInstanceState)



        viewModel.inviteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success<List<Orgs>> -> {

                    var dataArrayList: ArrayList<Orgs?> = it.value as ArrayList<Orgs?>
                    if(dataArrayList.size == 0){
                        Toast.makeText(requireContext(), "No Invites Found", Toast.LENGTH_SHORT).show()
                        view?.findNavController()?.navigate(R.id.fragment_item)

                    }
                    var listView : ListView = binding.listView
                    inviteListAdapter = InviteListAdapter(requireContext(), dataArrayList)
                    inviteListAdapter.listener = object :
                        InviteListAdapter.OnItemButtonClickListener {
                        override fun onItemButtonClick(position: Int) {
                            val invite: Orgs? = dataArrayList[position]
                            if (invite != null) {
                                acceptInv(invite)
                            }
                            Toast.makeText(requireContext(), "Accepted: ${invite?.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    listView.adapter = inviteListAdapter
                }

                is Resource.Failure -> {
                    if(it.isNetworkError) {
                        Toast.makeText(requireContext(),"please check your internet and try again", Toast.LENGTH_LONG).show()
                    }else if(it.errorCode == HttpStatus.SC_UNAUTHORIZED){
                        requireActivity().startNewActivity(LoginActivity::class.java)
                    }else{
                        Toast.makeText(requireContext(),"an error occured, please try again later", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        viewModel.acceptResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success<String> -> {
                    viewModel.getInvites()
                }
                is Resource.Failure -> {
                    if(it.isNetworkError) {
                        Toast.makeText(requireContext(),"please check your internet and try again", Toast.LENGTH_LONG).show()
                    }else if(it.errorCode == HttpStatus.SC_UNAUTHORIZED){
                        requireActivity().startNewActivity(LoginActivity::class.java)
                    }else{
                        Toast.makeText(requireContext(),"an error occured, please try again later", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        return v
    }
    fun acceptInv(invite : Orgs){
        viewModel.acceptInvite(invite.id, invite.role)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.skipButton.setOnClickListener{
            view.findNavController().navigate(R.id.fragment_org)
        }
//        (activity as DrawerLocker).setDrawerEnabled(false)
        viewModel.getInvites()
    }

    override fun getViewModel() = InviteViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentInviteBinding.inflate(inflater, container, false)

    override fun getRepository() : InviteRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        return InviteRepository(serverComms.buildApi(InviteAPI::class.java, token), userPreferences)
    }

    override fun onResume() {
        super.onResume()
//        (activity as? DrawerLocker)?.setDrawerEnabled(false)
        viewModel.getInvites()
    }

}