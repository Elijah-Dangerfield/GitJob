package com.dangerfield.gitjob.model.mapquest

data class Location(
    val adminArea1: String,
    val adminArea1Type: String,
    val adminArea3: String,
    val adminArea3Type: String,
    val adminArea4: String,
    val adminArea4Type: String,
    val adminArea5: String,
    val adminArea5Type: String,
    val adminArea6: String,
    val adminArea6Type: String,
    val displayLatLng: DisplayLatLng,
    val dragPoint: Boolean,
    val geocodeQuality: String,
    val geocodeQualityCode: String,
    val latLng: LatLng,
    val linkId: String,
    val postalCode: String,
    val sideOfStreet: String,
    val street: String,
    val type: String,
    val unknownInput: String
)