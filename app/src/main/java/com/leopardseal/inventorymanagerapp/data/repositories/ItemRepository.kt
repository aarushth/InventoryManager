package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.Resource
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
    private var isFullItemListCached = false
    suspend fun getItems() = safeApiCall {
        api.getItems(preferences.orgId.first()!!)
    }
    suspend fun getItemsByBoxId(boxId : Long) = safeApiCall {
        api.getItemsByBoxId(preferences.orgId.first()!!, boxId)
    }
    private var cachedItems: List<Item> = listOf()

    fun setCachedItems(items: List<Item>) {
        cachedItems = items
        isFullItemListCached = true
    }
    fun isItemListFullyCached(): Boolean {
        return isFullItemListCached
    }
    fun getCachedItems(): Resource<List<Item>> {
        if(cachedItems.isEmpty()){
            return Resource.Failure(false, null)
        }else{
            return Resource.Success<List<Item>>(cachedItems)
        }
    }
    fun getCachedItemsByBoxId(boxId : Long) : List<Item>{
        return cachedItems.filter { it.boxId == boxId }
    }
    fun updateCachedItem(item: Item) {
        val index = cachedItems.indexOfFirst { it.id == item.id }
        cachedItems = if (index != -1) {
            cachedItems.toMutableList().apply { this[index] = item }
        } else {
            cachedItems + item
        }
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
    fun clearCache(){
        cachedItems = emptyList()
        isFullItemListCached  = false
    }

}