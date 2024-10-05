package com.example.haechorom.api.dto.request

data class JosaPostRequest(
    val josaName: String,
    val coastName: String,
    val lat: Double,
    val lng: Double,
    val coastLength: Int,
    val collectBag: Int,
    val trashType: String,
    val josaStatus: String,
    val josaImg: String
)
