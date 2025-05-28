package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.dto.SearchResponse
import retrofit2.Response

import retrofit2.http.GET

import retrofit2.http.Path

interface SearchAPI {


    @GET("search/{org_id}/{query}")
    suspend fun search(
        @Path("org_id") orgId : Long,
        @Path("query") query : String
    ) : Response<SearchResponse>

    @GET("search_barcode/{org_id}/{barcode}")
    suspend fun searchBarcode(
        @Path("org_id") orgId : Long,
        @Path("barcode") barcode : String
    ) : Response<SearchResponse>

}