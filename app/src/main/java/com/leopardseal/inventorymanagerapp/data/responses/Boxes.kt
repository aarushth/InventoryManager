package com.leopardseal.inventorymanagerapp.data.responses

data class Boxes (var id: Long? = null,
             var name: String,
             var orgId: Long,
             var barcode: String?,
             var locationId: Long?,
             var size: String?,
             var imageUrl: String?)