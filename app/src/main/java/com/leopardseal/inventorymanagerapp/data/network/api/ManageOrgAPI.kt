package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.dto.ManageOrgsResponse
import com.leopardseal.inventorymanagerapp.data.responses.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ManageOrgAPI{

    @GET("user_list/{org_id}")
    suspend fun getUserList(
        @Path("org_id") orgId : Long
    ) : Response<ManageOrgsResponse>

    @POST("remove_user/{org_id}")
    suspend fun removeUser(
        @Path("org_id") orgId : Long,
        @Body userResponse: UserResponse
    ) : Response<Unit>

    @POST("remove_invite/{org_id}")
    suspend fun removeInvite(
        @Path("org_id") orgId : Long,
        @Body userResponse: UserResponse
    ) : Response<Unit>

    @POST("invite_user/{org_id}")
    suspend fun invite(
        @Path("org_id") orgId : Long,
        @Body userResponse: UserResponse
    ) : Response<Unit>
}