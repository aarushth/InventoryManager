package com.leopardseal.inventorymanagerapp.data.repositories

@Singleton
class BoxRepository @Inject constructor(
    private val api: SearchAPI,
    private val preferences: UserPreferences
): BaseRepository() {


    suspend fun search(query : String) = safeApiCall{
        api.search(preferences.orgId.first()!!, query)
    }

}