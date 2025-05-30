package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.ManageOrgAPI
import com.leopardseal.inventorymanagerapp.data.responses.dto.UserResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageOrgRepository @Inject constructor(
    private val api : ManageOrgAPI,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    suspend fun getUserList() = safeApiCall {
        api.getUserList(userPreferences.orgId.first()!!)
    }

    suspend fun removeUser(user : UserResponse) = safeApiCall {
        api.removeUser(userPreferences.orgId.first()!!, user)
    }

    suspend fun removeInvite(user : UserResponse) = safeApiCall {
        api.removeInvite(userPreferences.orgId.first()!!, user)
    }

    suspend fun invite(user : UserResponse) = safeApiCall {
        api.invite(userPreferences.orgId.first()!!, user)
    }
}