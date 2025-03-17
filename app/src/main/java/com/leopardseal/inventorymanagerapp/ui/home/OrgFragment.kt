package com.leopardseal.inventorymanagerapp.ui.home

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.leopardseal.inventorymanagerapp.R


/**
 * A fragment representing a list of Items.
 */
class OrgFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_org, container, false)


        return view
    }
}