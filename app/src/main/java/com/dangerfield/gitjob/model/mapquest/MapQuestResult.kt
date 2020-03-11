package com.dangerfield.gitjob.model.mapquest

data class MapQuestResult(
    val info: Info,
    val options: Options,
    val results: List<Result>
)