package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.ItemAPI
import com.leopardseal.inventorymanagerapp.data.network.API.LoginAPI
import kotlinx.coroutines.flow.first

class ItemRepository(
    private val api: ItemAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getItems() = safeApiCall {
        api.getItems(preferences.orgId.first()!!)
    }


}