package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.ItemAPI
import com.leopardseal.inventorymanagerapp.data.responses.Item
import kotlinx.coroutines.flow.first
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
    suspend fun getItemsByBoxId(boxId : Long) = safeApiCall {
        api.getItemsByBoxId(preferences.orgId.first()!!, boxId)
    }
    private var cachedItems: List<Item> = listOf()

    fun setCachedItems(items: List<Item>) {
        cachedItems = items
    }

    fun getCachedItemById(id: Long): Item? {
        return cachedItems.find { it.id == id }
    }

    suspend fun fetchItemById(id: Long) = safeApiCall {
        api.getItemById(id)
    }
    suspend fun updateItem(item : Item, imageChanged : Boolean) = safeApiCall  {
        api.updateItem(item, imageChanged)
    }

}