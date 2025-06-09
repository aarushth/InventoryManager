package com.leopardseal.inventorymanagerapp.data.responses.dto

import com.leopardseal.inventorymanagerapp.data.responses.MyUser

data class LoginResponse(
    val token: String,
    val user: MyUser
)