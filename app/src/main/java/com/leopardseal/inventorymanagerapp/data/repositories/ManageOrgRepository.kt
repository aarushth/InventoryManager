package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.ManageOrgAPI
import com.leopardseal.inventorymanagerapp.data.responses.Role
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

    suspend fun removeUser(userId : Long) = safeApiCall {
        api.removeUser(userPreferences.orgId.first()!!, userId)
    }

    suspend fun removeInvite(userId : Long) = safeApiCall {
        api.removeInvite(userPreferences.orgId.first()!!, userId)
    }

    suspend fun invite(email: String, role : Role) = safeApiCall {
        api.invite(userPreferences.orgId.first()!!, email, role)
    }
}