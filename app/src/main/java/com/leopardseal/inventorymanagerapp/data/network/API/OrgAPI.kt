package com.leopardseal.inventorymanagerapp.data.network.API

import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import retrofit2.Response
import retrofit2.http.GET

interface OrgAPI {

    @GET("get_orgs")
    suspend fun getOrgs() : Response<List<Orgs>>
}