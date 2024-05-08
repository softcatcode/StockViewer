package com.softcat.stockviewer.presentation.stockPlot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.softcat.stockviewer.presentation.stockPlot.stateContent.BarPlot
import com.softcat.stockviewer.presentation.stockPlot.stateContent.LoadingPlot
import org.koin.androidx.compose.koinViewModel

@Composable
fun StockScreenContent() {
    val viewModel = koinViewModel<StockViewModel>()
    val state = viewModel.state.collectAsState(initial = StockScreenState.Initial)

    when (val currentState = state.value) {
        is StockScreenState.Bars -> BarPlot(currentState.barList, currentState.timeFrame) {
            viewModel.loadBars(it)
        }

        StockScreenState.Loading -> LoadingPlot()

        StockScreenState.Initial -> {}
    }
}