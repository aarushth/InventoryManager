package com.leopardseal.inventorymanagerapp.data.repositories

import android.content.Context
import android.net.Uri
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.ImageAPI
import com.leopardseal.inventorymanagerapp.data.network.API.BoxAPI
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoxRepository @Inject constructor(
    private val api: BoxAPI,
    private val imageApi : ImageAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getBoxes() = safeApiCall {
        api.getBoxes(preferences.orgId.first()!!)
    }
    private var cachedBoxes: List<Boxes> = listOf()

    fun setCachedBoxes(boxes: List<Boxes>) {
        cachedBoxes = boxes
    }

    fun getCachedBoxById(id: Long): Boxes? {
        return cachedBoxes.find { it.id == id }
    }

    suspend fun fetchBoxById(id: Long) = safeApiCall {
        api.getBoxById(id)
    }
    suspend fun updateBox(box : Boxes, imageChanged : Boolean) = safeApiCall  {
        api.updateBox(box, imageChanged)
    }

}