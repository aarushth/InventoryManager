package com.leopardseal.inventorymanagerapp.data.responses.dto

import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Location

data class SearchResponse(
    val itemCount : Int,
    val boxCount : Int,
    val locationCount : Int,
    val items : List<Item>,
    val boxes : List<Box>,
    val locations : List<Location>)