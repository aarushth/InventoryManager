package com.leopardseal.inventorymanagerapp.data.network.api

import retrofit2.Response
import retrofit2.http.GET

interface ManageOrgAPI{

    @GET("user_list/{org_id}")
    suspend fun getUserList(
        @Path("org_id") orgId : Long
    ) : Response<List<UserResponse>>
}