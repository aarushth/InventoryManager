package com.leopardseal.inventorymanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}