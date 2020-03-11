package com.dangerfield.gitjob.model

data class JobListing(
    val company: String,
    val company_logo: String,
    val company_url: String,
    val created_at: String,
    val description: String,
    val how_to_apply: String,
    val id: String,
    val location: String,
    val title: String,
    val type: String,
    val url: String
)