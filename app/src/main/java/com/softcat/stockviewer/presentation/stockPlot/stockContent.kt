package com.softcat.stockviewer.presentation.stockPlot

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun StockScreenContent(viewModel: StockViewModel) {
    val state = viewModel.state.collectAsState(initial = StockScreenState.Initial)
    when (val currentState = state.value) {
        is StockScreenState.Bars -> {
            BarCanvas(currentState.barList)
            Log.i("StockScreenContent", "Bars: ${currentState.barList}")
        }
        StockScreenState.Initial -> {
            Log.i("StockScreenContent", "Initial")
        }
    }
}