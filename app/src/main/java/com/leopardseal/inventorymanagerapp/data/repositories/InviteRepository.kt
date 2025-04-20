package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.InviteAPI
import com.leopardseal.inventorymanagerapp.data.network.API.LoginAPI

class InviteRepository(
    private val api: InviteAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getInvites() = safeApiCall {
        api.getInvites()
    }

    suspend fun acceptInvite(orgId:Long, role: String) = safeApiCall {
        api.acceptInvite(orgId, role)
    }

}