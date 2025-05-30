package com.leopardseal.inventorymanagerapp.data.responses.dto

data class ManageOrgsResponse(
    val users : List<UserResponse>,
    val invites : List<UserResponse>
)
