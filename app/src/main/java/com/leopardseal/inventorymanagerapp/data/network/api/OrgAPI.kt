package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Org
import com.leopardseal.inventorymanagerapp.data.responses.UserRole
import retrofit2.Response
import retrofit2.http.GET

interface OrgAPI {

    @GET("get_orgs")
    suspend fun getOrgs() : Response<List<UserRole>>
}