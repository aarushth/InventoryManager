package com.leopardseal.inventorymanagerapp.data.responses

data class Orgs (var id: Long, var name: String, var imageUrl: String?){
    override fun toString(): String {
        return name
    }
}