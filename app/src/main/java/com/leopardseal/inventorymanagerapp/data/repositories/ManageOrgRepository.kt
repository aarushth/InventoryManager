package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.network.api.SettingAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageOrgRepository @Inject constructor(
    private val api : ManageOrgAPI
) : BaseRepository() {

    suspend fun getUserList() = safeApiCall {
        api.getUserList()
    }
}