package com.softcat.stockviewer.domain.entities

import android.icu.util.Calendar
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Immutable
@Parcelize
data class Bar(
    val open: Float,
    val close: Float,
    val min: Float,
    val max: Float,
    val time: Long
): Parcelable {
    val calendar: Calendar
        get() = Calendar.getInstance().apply {
            time = Date(this@Bar.time)
        }
}
