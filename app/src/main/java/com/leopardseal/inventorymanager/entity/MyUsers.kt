package com.leopardseal.inventorymanager.entity

import kotlinx.serialization.Serializable

@Serializable
data class MyUsers (var id: Long, var email: String, var picture: String?)