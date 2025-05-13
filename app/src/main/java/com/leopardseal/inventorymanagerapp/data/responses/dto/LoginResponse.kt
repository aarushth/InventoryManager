package com.leopardseal.inventorymanagerapp.data.responses.dto

import com.leopardseal.inventorymanagerapp.data.responses.MyUsers

data class LoginResponse(
    val token: String,
    val user: MyUsers
)