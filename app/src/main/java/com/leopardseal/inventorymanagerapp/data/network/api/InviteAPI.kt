package com.leopardseal.inventorymanagerapp.data.network.api

import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import retrofit2.Response

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface InviteAPI {
    @GET("get_invites")
    suspend fun getInvites() : Response<List<Orgs>>

    @FormUrlEncoded
    @POST("accept_invite")
    suspend fun acceptInvite(
        @Field("orgId") orgId: Long,
        @Field("role") role: String
    ) : Response<Unit>
}