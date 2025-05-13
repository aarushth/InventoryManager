package com.leopardseal.inventorymanagerapp.ui.main.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R


import com.leopardseal.inventorymanagerapp.data.network.Resource


import com.leopardseal.inventorymanagerapp.data.responses.Items

import com.leopardseal.inventorymanagerapp.databinding.FragmentItemBinding
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity

import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ItemFragment : Fragment() {


    private lateinit var itemsListAdapter : ItemsListAdapter

    private val viewModel : ItemViewModel by viewModels()
    private lateinit var binding : FragmentItemBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemBinding.bind(view)
        navController = view.findNavController()

        viewModel.itemResponse.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            when (it) {
                is Resource.Success<List<Items>> -> {
                    var dataArrayList: ArrayList<Items?> = it.value as ArrayList<Items?>
                    if(dataArrayList.size == 0){
                        binding.itemText.text = "It looks like this org doesn't have any items. Click + to add an item"
                    }else{
                        binding.itemText.text = "Items:"
                    }
                    val recyclerView = binding.itemsRecyclerView
                    recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                    recyclerView.adapter = ItemsListAdapter(dataArrayList.toList(), object : ItemsListAdapter.OnItemClickListener {
                        override fun onItemClick(item: Items) {

                            val action = ItemFragmentDirections.actionItemFragmentToItemExpandedFragment(item.id)
                            navController.navigate(action)
                        }
                    })


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
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getItems()
        }
        viewModel.getItems()
    }
    override fun onResume() {
        super.onResume()
        viewModel.getItems()
    }


}