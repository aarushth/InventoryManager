package com.leopardseal.inventorymanagerapp.data.responses

data class Box (
    var id: Long? = null,
    var name: String,
    var orgId: Long,
    var barcode: String?,
    var locationId: Long?,
    var size: BoxSize?,
    var imageUrl: String?
)