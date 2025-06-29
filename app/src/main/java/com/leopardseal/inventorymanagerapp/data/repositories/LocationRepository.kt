package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.network.api.LocationAPI
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Location
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: LocationAPI,
    private val preferences: UserPreferences
): BaseRepository() {
    private var isFullLocationListCached = false
    suspend fun getLocations() = safeApiCall {
        api.getLocations(preferences.orgId.first()!!)
    }
    private var cachedLocations: List<Location> = listOf()

    fun setCachedLocations(locations: List<Location>) {
        cachedLocations = locations
    }
    fun getCachedLocations() : Resource<List<Location>>{
        if(cachedLocations.isEmpty()){
            return Resource.Failure(false, null)
        }else{
            return Resource.Success<List<Location>>(cachedLocations)
        }
    }
    fun updateCachedLocation(location: Location) {
        val index = cachedLocations.indexOfFirst { it.id == location.id }
        cachedLocations = if (index != -1) {
            cachedLocations.toMutableList().apply { this[index] = location }
        } else {
            cachedLocations + location
        }
    }
    fun isLocationListFullyCached(): Boolean {
        return isFullLocationListCached
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
    fun clearCache(){
        cachedLocations = emptyList()
        isFullLocationListCached  = false
    }
}