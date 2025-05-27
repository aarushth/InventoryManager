package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Path

interface SearchAPI {


    @GET("search/{org_id}/{query}")
    suspend fun search(
        @Path("org_id") orgId : Long,
        @Path("query") query : String
    ) : Response<SearchResponse>

}