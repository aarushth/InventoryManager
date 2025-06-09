package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.ImageAPI
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

    suspend fun getBoxes() = safeApiCall {
        api.getBoxes(preferences.orgId.first()!!)
    }
    suspend fun getBoxesByLocationId(locationId : Long) = safeApiCall {
        api.getBoxesByLocationId(preferences.orgId.first()!!, locationId)
    }
    private var cachedBoxes: List<Box> = listOf()

    fun setCachedBoxes(boxes: List<Box>) {
        cachedBoxes = boxes
    }

    fun getCachedBoxById(id: Long): Box? {
        return cachedBoxes.find { it.id == id }
    }

    suspend fun fetchBoxById(id: Long) = safeApiCall {
        api.getBoxById(id)
    }
    suspend fun updateBox(box : Box, imageChanged : Boolean) = safeApiCall  {
        api.updateBox(box, imageChanged)
    }

}