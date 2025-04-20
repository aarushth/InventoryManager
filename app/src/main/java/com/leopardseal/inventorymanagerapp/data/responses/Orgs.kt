package com.leopardseal.inventorymanagerapp.data.responses

data class Orgs (var id: Long, var name: String, var imageUrl: String?, var role: String){
    override fun toString(): String {
        return name
    }
}