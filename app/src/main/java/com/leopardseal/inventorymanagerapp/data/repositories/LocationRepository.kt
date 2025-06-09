package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.LocationAPI
import com.leopardseal.inventorymanagerapp.data.responses.Location
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: LocationAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getLocations() = safeApiCall {
        api.getLocations(preferences.orgId.first()!!)
    }
    private var cachedLocations: List<Location> = listOf()

    fun setCachedLocations(locations: List<Location>) {
        cachedLocations = locations
    }

    fun getCachedLocationById(id: Long): Location? {
        return cachedLocations.find { it.id == id }
    }

    suspend fun fetchLocationById(id: Long) = safeApiCall {
        api.getLocationById(id)
    }
    suspend fun updateLocation(location : Location, imageChanged : Boolean) = safeApiCall  {
        api.updateLocation(location, imageChanged)
    }

}