package com.leopardseal.inventorymanagerapp.ui.main.invite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import com.leopardseal.inventorymanagerapp.databinding.FragmentInviteBinding
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity

import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InviteFragment : Fragment() {

    private lateinit var inviteListAdapter : InviteListAdapter
    private val viewModel: InviteViewModel by viewModels()
    private lateinit var binding : FragmentInviteBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInviteBinding.bind(view)
        navController = view.findNavController()
        viewModel.inviteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success<List<Orgs>> -> {

                    var dataArrayList: ArrayList<Orgs?> = it.value as ArrayList<Orgs?>
                    if(dataArrayList.size == 0){
                        Toast.makeText(requireContext(), "No Invites Found", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_fragment_invite_to_fragment_item)

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
                is Resource.Success -> {
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


        binding.skipButton.setOnClickListener{
            navController.navigate(R.id.action_fragment_invite_to_fragment_org)
        }
        viewModel.getInvites()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getInvites()
    }


    fun acceptInv(invite : Orgs){
        viewModel.acceptInvite(invite.id, invite.role)
    }
}