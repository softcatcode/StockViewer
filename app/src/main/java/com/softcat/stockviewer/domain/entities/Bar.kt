package com.softcat.stockviewer.domain.entities

import android.icu.util.Calendar
import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
data class Bar(
    val open: Float,
    val close: Float,
    val min: Float,
    val max: Float,
    val time: Long
) {
    val calendar: Calendar
        get() = Calendar.getInstance().apply {
            time = Date(this@Bar.time)
        }
}
