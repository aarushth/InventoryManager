package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.LoginAPI
import com.leopardseal.inventorymanagerapp.data.network.OrgAPI

class OrgRepository(
    private val api: OrgAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getOrgs() = safeApiCall {
        api.getOrgs()
    }

}