package com.leopardseal.inventorymanagerapp.ui.home.org

import android.graphics.drawable.shapes.Shape
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.OrgAPI
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository

import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import com.leopardseal.inventorymanagerapp.databinding.FragmentOrgBinding
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment
import com.leopardseal.inventorymanagerapp.ui.home.HomeActivity
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class OrgFragment : BaseFragment<OrgViewModel, FragmentOrgBinding, OrgRepository>() {


    private lateinit var orgsListAdapter : OrgsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v : View? = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.orgResponse.observe(viewLifecycleOwner, Observer {
//            binding.progressBar.visible(false)
            when (it) {
                is Resource.Success<List<Orgs>> -> {
                    var dataArrayList: ArrayList<Orgs?> = it.value as ArrayList<Orgs?>
                    var listView : ListView = binding.listView
                    orgsListAdapter = OrgsListAdapter(requireContext(), dataArrayList)
                    listView.adapter = orgsListAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, i, l ->
                        var org :Orgs = listView.getItemAtPosition(i) as Orgs
                        Toast.makeText(requireContext(), "you selected ${org.name}", Toast.LENGTH_LONG).show()

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

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getOrgs()
    }

    override fun getViewModel() = OrgViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrgBinding.inflate(inflater, container, false)

    override fun getRepository() : OrgRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        return OrgRepository(serverComms.buildApi(OrgAPI::class.java, token), userPreferences)
    }
}