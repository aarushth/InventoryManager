package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.SearchAPI
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val api: SearchAPI,
    private val preferences: UserPreferences
): BaseRepository() {


    suspend fun search(query : String) = safeApiCall{
        api.search(preferences.orgId.first()!!, query)
    }

}