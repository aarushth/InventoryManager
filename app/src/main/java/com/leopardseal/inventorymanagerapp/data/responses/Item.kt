package com.leopardseal.inventorymanagerapp.data.responses

data class Item (var id: Long? = null,
             var name: String,
             var orgId: Long,
             var barcode: String?,
             var description: String?,
             var boxId: Long?,
             var quantity : Long,
             var alert : Long,
             var imageUrl: String?,
             var tags: List<Tag> = emptyList()
            )