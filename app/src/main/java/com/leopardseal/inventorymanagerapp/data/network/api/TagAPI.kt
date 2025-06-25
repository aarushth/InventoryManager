package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Tag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TagAPI {

    @GET("get_tags")
    suspend fun getTags() : Response<List<Tag>>

    @POST("add_tag/{tag}")
    suspend fun addTag(
        @Path("tag") tag : String) : Response<Tag>
}