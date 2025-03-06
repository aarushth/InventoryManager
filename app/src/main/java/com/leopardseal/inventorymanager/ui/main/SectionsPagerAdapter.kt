package com.leopardseal.inventorymanager.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.leopardseal.inventorymanager.R

//names of tabs
private val TAB_TITLES = arrayOf(
    "Items"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment.
        when(position){
            0 -> return ItemsFragment.newInstance();
            
        }
        return PlaceholderFragment.newInstance(position + 1)
    }

    //sets name of Tabs/Fragments
    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 1
    }
}