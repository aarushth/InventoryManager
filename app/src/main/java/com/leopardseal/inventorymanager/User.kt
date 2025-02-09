package com.leopardseal.inventorymanager

import kotlinx.serialization.Serializable

@Serializable
data class User (var id: Int,var email: String, var picture: String)