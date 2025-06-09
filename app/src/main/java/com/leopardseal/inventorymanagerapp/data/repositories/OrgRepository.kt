package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.OrgAPI
import com.leopardseal.inventorymanagerapp.data.responses.Org
import javax.inject.Inject

class OrgRepository @Inject constructor(
    private val api: OrgAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getOrgs() = safeApiCall {
        api.getOrgs()
    }

    suspend fun saveOrg(org: Org){
        preferences.saveOrgId(org.id)
        if(org.imageUrl != null) {
            preferences.saveOrgImg(org.imageUrl!!)
        }else{
            preferences.saveOrgImg("")
        }
        preferences.saveOrgName(org.name)
        preferences.saveOrgRole(org.role)
    }
}