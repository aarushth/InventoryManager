package com.leopardseal.inventorymanagerapp.ui.main.org

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.data.network.Resource


import com.leopardseal.inventorymanagerapp.data.responses.Orgs

import com.leopardseal.inventorymanagerapp.databinding.FragmentOrgBinding
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class OrgFragment : Fragment() {


    private lateinit var orgsListAdapter : OrgsListAdapter
    private val viewModel : OrgViewModel by viewModels()
    private lateinit var binding : FragmentOrgBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_org, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrgBinding.bind(view)
        navController = view.findNavController()

        viewModel.orgResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success<List<Orgs>> -> {
                    var dataArrayList: ArrayList<Orgs?> = it.value as ArrayList<Orgs?>
                    var listView : ListView = binding.listView
                    orgsListAdapter = OrgsListAdapter(requireContext(), dataArrayList)
                    listView.adapter = orgsListAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, i, l ->
                        var org :Orgs = listView.getItemAtPosition(i) as Orgs
                        viewModel.saveOrg(org)
                    }
                }

                is Resource.Failure -> {
                    if(it.isNetworkError) {
                        Toast.makeText(requireContext(),"please check your internet and try again", Toast.LENGTH_LONG).show()
                    }else if(it.errorCode == HttpStatus.SC_UNAUTHORIZED){
                        requireActivity().startNewActivity(LoginActivity::class.java)
                    }else{
                        Toast.makeText(requireContext(),"an error occured, please try again later",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orgSaved.collect { saved ->
                if (saved) {
                    navController.navigate(R.id.action_fragment_org_to_fragment_item)
                    viewModel.resetOrgSavedFlag()
                }
            }
        }
        viewModel.getOrgs()
    }


    override fun onResume() {
        super.onResume()
        viewModel.getOrgs()
    }
}