package com.leopardseal.inventorymanagerapp.data.network.API

import com.leopardseal.inventorymanagerapp.data.responses.Items
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Path

interface ItemAPI {


    @GET("get_items/{org_id}")
    suspend fun getItems(
        @Path("org_id") orgId : Long
    ) : Response<List<Items>>

    @GET("get_item/{item_id}")
    suspend fun getItemById(
        @Path("item_id") itemId : Long
    ) : Response<Items>

    @POST("update_item")
    suspend fun updateItem(
        @Body items: Items
    ) : Response<Unit>
}