package com.leopardseal.inventorymanagerapp.ui.login

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.leopardseal.inventorymanagerapp.R



class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        actionBar?.hide()
    }
}