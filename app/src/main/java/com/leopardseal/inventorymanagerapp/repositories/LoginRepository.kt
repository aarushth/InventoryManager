package com.leopardseal.inventorymanagerapp.repositories

import com.leopardseal.inventorymanagerapp.network.LoginAPI

class LoginRepository(
    private val api: LoginAPI
):BaseRepository() {

    suspend fun login(
    ) = safeApiCall {

    }
}