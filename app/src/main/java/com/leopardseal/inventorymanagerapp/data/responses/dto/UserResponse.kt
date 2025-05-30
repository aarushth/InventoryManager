package com.leopardseal.inventorymanagerapp.data.responses.dto

data class UserResponse(
    val id : Long?,
    val email : String,
    val imgUrl : String?,
    val role : String) {
}