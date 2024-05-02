package com.softcat.stockviewer.presentation.stockPlot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.softcat.stockviewer.ui.theme.StockViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockViewerTheme {
                StockScreenContent()
            }
        }
    }
}