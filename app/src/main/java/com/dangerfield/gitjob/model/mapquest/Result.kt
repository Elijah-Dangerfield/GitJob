package com.dangerfield.gitjob.model.mapquest

data class Result(
    val locations: List<Location>,
    val providedLocation: ProvidedLocation
)