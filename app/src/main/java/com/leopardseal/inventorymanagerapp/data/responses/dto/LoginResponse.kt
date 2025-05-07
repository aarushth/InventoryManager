package com.leopardseal.inventorymanagerapp.data.responses.

data class LoginResponse(
    val token: String,
    val user: MyUser
)