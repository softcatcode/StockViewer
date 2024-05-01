package com.softcat.stockviewer.presentation.stockPlot

import com.softcat.stockviewer.domain.entities.Bar

sealed class StockScreenState {
    data object Initial: StockScreenState()

    data class Bars(val barList: List<Bar>): StockScreenState()
}