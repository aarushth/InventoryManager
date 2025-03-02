package com.leopardseal.inventorymanager.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserRoles (var id: Long, var userId: Long, var orgId: Long, var roleId: Long)