package com.dangerfield.gitjob.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "JOB_LISTINGS")
@Parcelize
data class JobListing(
    var company: String? = null,
    var company_logo: String? = null,
    var company_url: String? = null,
    var created_at: String? = null,
    var description: String? = null,
    var how_to_apply: String? = null,
    @PrimaryKey var id: String,
    var location: String? = null,
    var title: String? = null,
    var type: String? = null,
    var url: String? = null,
   @Ignore  var saved: Boolean? = false
) : Parcelable {
    constructor() : this(
        "","","","","","","","","","",""
    )

    fun toSaveable(): SavedJob {
        return SavedJob(this.company,this.company_logo, this.company_url, this.created_at, this.description,this.how_to_apply, this.id, this.location, this.title, this.type)
    }
}