package com.leopardseal.inventorymanagerapp.data.repositories

@Singleton
class SettingRepository @Inject constructor(
    private val api : SettingAPI
) : BaseRepository() {

    suspend fun getVersion() = safeApiCall {
        api.getVersion()
    }
}