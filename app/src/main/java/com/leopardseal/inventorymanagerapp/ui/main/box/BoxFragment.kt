package com.leopardseal.inventorymanagerapp.ui.main.box

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.leopardseal.inventorymanagerapp.databinding.FragmentBoxBinding


class BoxFragment : Fragment() {

private var _binding: FragmentBoxBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val boxViewModel =
            ViewModelProvider(this).get(BoxViewModel::class.java)

    _binding = FragmentBoxBinding.inflate(inflater, container, false)
    val root: View = binding.root

//      (activity as DrawerLocker).setDrawerEnabled(true)

    val textView: TextView = binding.textBox
    boxViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}