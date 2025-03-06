package com.leopardseal.inventorymanager

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.leopardseal.inventorymanager.ui.main.SectionsPagerAdapter
import com.leopardseal.inventorymanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit val serverComms:ServerComms
    lateinit val signInResponse:SignInResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Adapter to handle all tabs
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        //connecting views to hold tabs, connected to adapter
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        //tab names on top of screen
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        //button
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        serverComms = intent.getExtra("ServerComms")
        signInResponse = intent.getExtra("SignInRespose")
    }
}