package com.dangerfield.gitjob.model.mapquest

data class Options(
    val ignoreLatLngInput: Boolean,
    val maxResults: Int,
    val thumbMaps: Boolean
)