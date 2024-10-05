package com.example.haechorom.api.dto.response

data class CollectPostResponse(
    val id: Long,
    val cleanName: String,
    val serialNumber: String,
    val josaId: Long,
    val cleanDate: String,
    val coastName: String,
    val lat: Double,
    val lng: Double,
    val coastLength: Int,
    val collectBag: Int,
    val collectVal: Int,
    val trashType: String,
    val cleanStatus: String
)
