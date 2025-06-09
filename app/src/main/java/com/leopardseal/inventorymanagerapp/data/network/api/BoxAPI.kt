package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Path

interface BoxAPI {


    @GET("get_boxes/{org_id}")
    suspend fun getBoxes(
        @Path("org_id") orgId : Long
    ) : Response<List<Box>>



    @GET("get_box/{box_id}")
    suspend fun getBoxById(
        @Path("box_id") boxId : Long
    ) : Response<Box>

    @GET("get_boxes_by_location_id/{org_id}/{location_id}")
    suspend fun getBoxesByLocationId(
        @Path("org_id") orgId : Long,
        @Path("location_id") locationId : Long
    ) : Response<List<Box>>

    @POST("update_box/{img_changed}")
    suspend fun updateBox(
        @Body boxes: Box,
        @Path("img_changed") imgChanged : Boolean
    ) : Response<SaveResponse>

}