package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Boxes
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
    ) : Response<List<Boxes>>

    @GET("get_box/{box_id}")
    suspend fun getBoxById(
        @Path("box_id") boxId : Long
    ) : Response<Boxes>

    @POST("update_box/{img_changed}")
    suspend fun updateBox(
        @Body boxes: Boxes,
        @Path("img_changed") imgChanged : Boolean
    ) : Response<SaveResponse>

}