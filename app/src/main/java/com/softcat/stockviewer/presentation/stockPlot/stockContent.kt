package com.softcat.stockviewer.presentation.stockPlot

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun StockScreenContent(viewModel: StockViewModel) {
    val state by viewModel.state.collectAsState(initial = StockScreenState.Initial)
    when (state) {
        is StockScreenState.Bars -> {
            Log.i("StockScreenContent", "Bars")
        }
        StockScreenState.Initial -> {
            Log.i("StockScreenContent", "Initial")
        }
    }
}