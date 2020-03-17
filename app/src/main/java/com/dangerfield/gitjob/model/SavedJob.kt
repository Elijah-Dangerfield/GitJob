package com.dangerfield.gitjob.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "SAVED_JOB_LISTINGS")
@Parcelize
data class SavedJob(
    val company: String? = null,
    val company_logo: String? = null,
    val company_url: String? = null,
    val created_at: String? = null,
    val description: String? = null,
    val how_to_apply: String? = null,
    @PrimaryKey val id: String,
    val location: String? = null,
    val title: String? = null,
    val type: String? = null,
    val url: String? = null
) : Parcelable {
    constructor() : this(
        "","","","","","","","","","",""
    )
}
