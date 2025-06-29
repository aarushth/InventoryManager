package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.network.api.BoxAPI
import com.leopardseal.inventorymanagerapp.data.responses.Box
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoxRepository @Inject constructor(
    private val api: BoxAPI,
    private val preferences: UserPreferences
): BaseRepository() {
    private var isFullBoxListCached = false

    suspend fun getBoxes() = safeApiCall {
        api.getBoxes(preferences.orgId.first()!!)
    }
    suspend fun getBoxesByLocationId(locationId : Long) = safeApiCall {
        api.getBoxesByLocationId(preferences.orgId.first()!!, locationId)
    }
    private var cachedBoxes: List<Box> = listOf()

    fun setCachedBoxes(boxes: List<Box>) {
        cachedBoxes = boxes
        isFullBoxListCached = true
    }
    fun getCachedBoxes() :Resource<List<Box>>{
        if(cachedBoxes.isEmpty()){
            return Resource.Failure(false, null)
        }else{
            return Resource.Success<List<Box>>(cachedBoxes)
        }
    }

    fun isBoxListFullyCached(): Boolean {
        return isFullBoxListCached
    }
    fun updateCachedBox(box: Box) {
        val index = cachedBoxes.indexOfFirst { it.id == box.id }
        cachedBoxes = if (index != -1) {
            cachedBoxes.toMutableList().apply { this[index] = box }
        } else {
            cachedBoxes + box
        }
    }
    fun getCachedBoxById(id: Long): Box? {
        return cachedBoxes.find { it.id == id }
    }
    fun getCachedBoxesByLocationId(locationId: Long) : List<Box>{
        return cachedBoxes.filter { it.locationId == locationId }
    }

    suspend fun fetchBoxById(id: Long) = safeApiCall {
        api.getBoxById(id)
    }
    suspend fun updateBox(box : Box, imageChanged : Boolean) = safeApiCall  {
        api.updateBox(box, imageChanged)
    }
    fun clearCache(){
        cachedBoxes = emptyList()
        isFullBoxListCached  = false
    }
}