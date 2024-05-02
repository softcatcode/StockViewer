package com.softcat.stockviewer.presentation.stockPlot

import android.os.Parcelable
import com.softcat.stockviewer.domain.entities.Bar
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class StockPlotState(
    val barList: List<Bar>,
    val visibleBarCount: Int = 100,
    val screenWidth: Float = 0f,
    val scrolledBy: Float = 0f
): Parcelable {
    val barWidth: Float
        get() = screenWidth / visibleBarCount

    val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarCount).coerceAtMost(barList.size)
            return barList.subList(startIndex, endIndex)
        }
}
