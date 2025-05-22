package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Locations
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Path

interface LocationAPI {


    @GET("get_locations/{org_id}")
    suspend fun getLocations(
        @Path("org_id") orgId : Long
    ) : Response<List<Locations>>

    @GET("get_location/{location_id}")
    suspend fun getLocationById(
        @Path("location_id") locationId : Long
    ) : Response<Locations>

    @POST("update_location/{img_changed}")
    suspend fun updateLocation(
        @Body locations: Locations,
        @Path("img_changed") imgChanged : Boolean
    ) : Response<SaveResponse>

}