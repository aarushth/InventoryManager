package com.leopardseal.inventorymanagerapp.data.network

import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import retrofit2.http.GET

interface OrgAPI {
    @GET("get_orgs")
    suspend fun getOrgs() : List<Orgs>
}