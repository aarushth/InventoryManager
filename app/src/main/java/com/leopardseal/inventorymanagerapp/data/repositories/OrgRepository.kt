package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.OrgAPI
import com.leopardseal.inventorymanagerapp.data.responses.Org
import com.leopardseal.inventorymanagerapp.data.responses.UserRole
import javax.inject.Inject

class OrgRepository @Inject constructor(
    private val api: OrgAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun getOrgs() = safeApiCall {
        api.getOrgs()
    }

    suspend fun saveOrg(userRole: UserRole){
        preferences.saveOrgId(userRole.org.id)
        if(userRole.org.imageUrl != null) {
            preferences.saveOrgImg(userRole.org.imageUrl!!)
        }else{
            preferences.saveOrgImg("")
        }
        preferences.saveOrgName(userRole.org.name)
        preferences.saveOrgRole(userRole.role.role)
    }
}