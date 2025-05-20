package com.leopardseal.inventorymanagerapp.data.repositories

import android.content.Context
import android.net.Uri
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.ItemAPI
import com.leopardseal.inventorymanagerapp.data.responses.Items
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val api: ItemAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getItems() = safeApiCall {
        api.getItems(preferences.orgId.first()!!)
    }
    private var cachedItems: List<Items> = listOf()

    fun setCachedItems(items: List<Items>) {
        cachedItems = items
    }

    fun getCachedItemById(id: Long): Items? {
        return cachedItems.find { it.id == id }
    }

    suspend fun fetchItemById(id: Long) = safeApiCall {
        api.getItemById(id)
    }
    suspend fun updateItem(item : Items, imageChanged : Boolean) = safeApiCall  {
        api.updateItem(item, imageChanged)
    }

}