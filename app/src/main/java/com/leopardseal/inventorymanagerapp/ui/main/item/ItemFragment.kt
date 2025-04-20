package com.leopardseal.inventorymanagerapp.ui.main.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.data.network.API.ItemAPI
import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Items

import com.leopardseal.inventorymanagerapp.databinding.FragmentItemBinding
import com.leopardseal.inventorymanagerapp.ui.base.BaseFragment
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity

import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ItemFragment : BaseFragment<ItemViewModel, FragmentItemBinding, ItemRepository>() {

private lateinit var itemsListAdapter : ItemsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var v : View? = super.onCreateView(inflater, container, savedInstanceState)

        //        (activity as DrawerLocker).setDrawerEnabled(false)

        viewModel.itemResponse.observe(viewLifecycleOwner, Observer {
        //            binding.progressBar.visible(false)
            when (it) {
                is Resource.Success<List<Items>> -> {
                    var dataArrayList: ArrayList<Items?> = it.value as ArrayList<Items?>
                    if(dataArrayList.size == 0){
                        binding.itemText.text = "It looks like this org doesn't have any items. Click + to add an item"
                    }else{
                      binding.itemText.text = "Items:"
                    }
                    var listView : ListView = binding.listView
                    itemsListAdapter = ItemsListAdapter(requireContext(), dataArrayList)
                    listView.adapter = itemsListAdapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener{ adapterView, view, i, l ->
                        var item : Items = listView.getItemAtPosition(i) as Items

                        //            view.findNavController().navigate(R.id.fragment_item)
                        Toast.makeText(requireContext(), "you selected ${item.name}", Toast.LENGTH_LONG).show()
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
        viewModel.getItems()
    }
    override fun onResume() {
        super.onResume()
        viewModel.getItems()
    }

    override fun getViewModel() = ItemViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
    container: ViewGroup?
    ) = FragmentItemBinding.inflate(inflater, container, false)

    override fun getRepository(): ItemRepository {
        val token = runBlocking { userPreferences.authToken.first()}
        val orgId = runBlocking { userPreferences.orgId.first() }
        return ItemRepository(serverComms.buildApi(ItemAPI::class.java, token), userPreferences)
    }

}