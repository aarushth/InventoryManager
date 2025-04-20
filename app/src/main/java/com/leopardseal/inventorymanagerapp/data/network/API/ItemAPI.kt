package com.leopardseal.inventorymanagerapp.data.network.API

import com.leopardseal.inventorymanagerapp.data.responses.Items

import retrofit2.http.GET

import retrofit2.http.Path

interface ItemAPI {


    @GET("get_items/{org_id}")
    suspend fun getItems(
        @Path("org_id") orgId : Long
    ) : List<Items>

}