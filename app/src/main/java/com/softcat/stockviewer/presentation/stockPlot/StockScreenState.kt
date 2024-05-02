package com.softcat.stockviewer.presentation.stockPlot

import com.softcat.stockviewer.domain.entities.Bar
import com.softcat.stockviewer.domain.entities.TimeFrame

sealed class StockScreenState {
    data object Initial: StockScreenState()

    data object Loading: StockScreenState()

    data class Bars(
        val barList: List<Bar>,
        val timeFrame: TimeFrame
    ): StockScreenState()
}