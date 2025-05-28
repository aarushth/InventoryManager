package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.network.api.SettingAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val api : SettingAPI
) : BaseRepository() {

    suspend fun getVersion() = safeApiCall {
        api.getVersion()
    }
}