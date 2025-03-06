package com.leopardseal.inventorymanager.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.leopardseal.inventorymanager.R
import com.leopardseal.inventorymanager.databinding.FragmentMainBinding

/**
 * A placeholder fragment containing a simple view.
 */
class ItemsFragment : Fragment() {

    private lateinit var itemsViewModel: ItemsViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewModel stores data to be shown
        itemsViewModel = ViewModelProvider(this).get(ItemsViewModel::class.java).apply {
            setIndex("items")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding is connection to xml layout i think
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        //sectionLabel is name of textView
        val textView: TextView = binding.sectionLabel
    
        //this connects a live data to a xml component    
        itemsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
    //Instatiates Fragment and stores static data to be passed to it.
    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        // private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(): PlaceholderFragment {
            return PlaceholderFragment()
            // .apply {
            //     arguments = Bundle().apply {
            //         putInt(ARG_SECTION_NUMBER, sectionNumber)
            //     }
            // }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}