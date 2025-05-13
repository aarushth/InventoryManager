package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.OrgAPI
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import javax.inject.Inject

class OrgRepository @Inject constructor(
    private val api: OrgAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getOrgs() = safeApiCall {
        api.getOrgs()
    }

    suspend fun saveOrg(org: Orgs){
        preferences.saveOrgId(org.id)
        if(org.imageUrl != null) {
            preferences.saveOrgImg(org.imageUrl!!)
        }else{
            preferences.saveOrgImg("")
        }
        preferences.saveOrgName(org.name)
    }
}