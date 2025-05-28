package com.leopardseal.inventorymanagerapp.data.responses.dto

import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.Items

data class SearchResponse(
    val itemCount : Int,
    val boxCount : Int,
    val locationCount : Int,
    val items : List<Items>,
    val boxes : List<Boxes>,
    val locations : List<Boxes>)