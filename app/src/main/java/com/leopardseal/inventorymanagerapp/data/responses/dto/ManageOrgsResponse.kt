package com.leopardseal.inventorymanagerapp.data.responses.dto

import com.leopardseal.inventorymanagerapp.data.responses.Invite
import com.leopardseal.inventorymanagerapp.data.responses.UserRole

data class ManageOrgsResponse(
    val users : List<UserRole>,
    val invites : List<Invite>
)
