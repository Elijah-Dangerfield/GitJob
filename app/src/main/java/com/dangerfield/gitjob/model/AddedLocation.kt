package com.dangerfield.gitjob.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "ADDED_LOCATIONS")
@Parcelize
data class AddedLocation(@PrimaryKey val location: String): Parcelable {
    constructor() : this("")
}