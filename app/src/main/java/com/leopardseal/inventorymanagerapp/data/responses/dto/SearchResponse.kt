package com.leopardseal.inventorymanagerapp.data.responses.destination

data class SearchResponse(
    itemCount : Integer, 
    boxCount : Integer,
    locationCount : Integer,
    items : List<Items>,
    boxes : List<Boxes>,
    locations : List<Boxes>)