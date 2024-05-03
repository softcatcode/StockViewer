package com.softcat.stockviewer.presentation.stockPlot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softcat.stockviewer.presentation.stockPlot.stateContent.BarPlot
import com.softcat.stockviewer.presentation.stockPlot.stateContent.LoadingPlot

@Composable
fun StockScreenContent() {
    val viewModel: StockViewModel = viewModel()
    val state = viewModel.state.collectAsState(initial = StockScreenState.Initial)

    when (val currentState = state.value) {
        is StockScreenState.Bars -> BarPlot(currentState.barList, currentState.timeFrame) {
            viewModel.loadBars(it)
        }

        is StockScreenState.Loading -> LoadingPlot()

        is StockScreenState.Initial -> {}
    }
}