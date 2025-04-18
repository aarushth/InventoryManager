package com.leopardseal.inventorymanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.ui.home.HomeActivity
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.startNewActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences : UserPreferences = UserPreferences(this)
        this.startNewActivity(LoginActivity::class.java)
        actionBar?.hide()
//        val liveData = userPreferences.authToken.asLiveData()
//        liveData.observe(this, Observer{
//            val activity = if(it == null) LoginActivity::class.java else HomeActivity::class.java
//            this.startNewActivity(activity)
//        })


    }
}