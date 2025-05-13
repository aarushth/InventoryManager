package com.leopardseal.inventorymanagerapp.ui.main.item.expanded

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.databinding.FragmentItemExpandedBinding
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemExpandedFragment : Fragment() {



    private val viewModel: ItemExpandedViewModel by viewModels()
    private lateinit var binding: FragmentItemExpandedBinding
    private var originalQuantity: Long = 0
    private var currentQuantity: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_item_expanded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemExpandedBinding.bind(view)

        viewModel.item.observe(viewLifecycleOwner, Observer { item ->
            item?.let {
                Picasso.get()
                    .load(item.imageUrl)
                    .placeholder(R.drawable.default_img)
                    .into(binding.itemImage)

                binding.itemName.text = item.name
                binding.itemBarcode.text = item.barcode
                binding.itemDescription.text = item.description
                binding.boxPlaceholder.text = item.boxId?.toString() ?: "this is a box"

                originalQuantity = item.quantity
                currentQuantity = item.quantity
                binding.quantityText.text = "$currentQuantity"
                binding.updateQuantityButton.isEnabled = false
            }


        })
        binding.buttonIncrease.setOnClickListener {
            currentQuantity++
            updateQuantityUI()
        }

        binding.buttonDecrease.setOnClickListener {
            if (currentQuantity > 0) {
                currentQuantity--
                updateQuantityUI()
            }
        }
        binding.updateQuantityButton.setOnClickListener {
            viewModel.updateItemQuantity(currentQuantity)

        }

        viewModel.updateResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(),"quantity updated to $currentQuantity",Toast.LENGTH_LONG).show()
                    originalQuantity = currentQuantity
                    binding.updateQuantityButton.isEnabled = false
                }
                is Resource.Failure -> {
                    if (it.isNetworkError) {
                        Toast.makeText(requireContext(),"please check your internet and try again",Toast.LENGTH_LONG).show()
                    } else if (it.errorCode == HttpStatus.SC_UNAUTHORIZED) {
                        requireActivity().startNewActivity(LoginActivity::class.java)
                    } else {
                        Toast.makeText(requireContext(),"an error occured, please try again later",Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

    private fun updateQuantityUI() {
        binding.quantityText.text = currentQuantity.toString()
        binding.updateQuantityButton.isEnabled = (currentQuantity != originalQuantity)
    }
}