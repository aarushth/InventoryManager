package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.network.API.InviteAPI

import javax.inject.Inject

class InviteRepository @Inject constructor(
    private val api: InviteAPI
): BaseRepository() {

    suspend fun getInvites() = safeApiCall {
        api.getInvites()
    }

    suspend fun acceptInvite(orgId:Long, role: String) = safeApiCall {
        api.acceptInvite(orgId, role)
    }

}