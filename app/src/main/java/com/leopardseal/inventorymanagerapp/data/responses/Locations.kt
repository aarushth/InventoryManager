package com.leopardseal.inventorymanagerapp.data.responses

data class Locations ( val id: Long? = null,
                       val name: String,
                       val orgId: Long,
                       val barcode: String?,
                       val description: String?,
                       val imageUrl: String?)