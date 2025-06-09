package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Path

interface ItemAPI {


    @GET("get_items/{org_id}")
    suspend fun getItems(
        @Path("org_id") orgId : Long
    ) : Response<List<Item>>

    @GET("get_item/{item_id}")
    suspend fun getItemById(
        @Path("item_id") itemId : Long
    ) : Response<Item>

    @GET("get_items_by_box_id/{org_id}/{box_id}")
    suspend fun getItemsByBoxId(
        @Path("org_id") orgId : Long,
        @Path("box_id") boxId : Long
    ) : Response<List<Item>>



    @POST("update_item/{img_changed}")
    suspend fun updateItem(
        @Body items: Item,
        @Path("img_changed") imgChanged : Boolean
    ) : Response<SaveResponse>

}