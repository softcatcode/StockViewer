package com.softcat.stockviewer.presentation.stockPlot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import com.softcat.stockviewer.domain.entities.Bar
import kotlin.math.roundToInt

private const val MIN_VISIBLE_COUNT = 10

fun DrawScope.drawBar(
    minY: Float, maxY: Float,
    minPrice: Float, maxPrice: Float,
    index: Int, bar: Bar,
    barWidth: Float
) {
    val pixelsPerPrice = (maxY - minY) / (maxPrice - minPrice)
    val xOffset = size.width - barWidth * (index + 0.5f)
    val minPoint = Offset(xOffset, maxY - (bar.min - minPrice) * pixelsPerPrice)
    val maxPoint = Offset(xOffset, maxY - (bar.max - minPrice) * pixelsPerPrice)
    val openPoint = Offset(xOffset, maxY - (bar.open - minPrice) * pixelsPerPrice)
    val closePoint = Offset(xOffset, maxY - (bar.close - minPrice) * pixelsPerPrice)
    val color = if (bar.open < bar.close) Color.Green else Color.Red
    drawLine(
        color = Color.White,
        start = minPoint,
        end = maxPoint,
        strokeWidth = 1f
    )
    drawLine(
        color = color,
        start = openPoint,
        end = closePoint,
        strokeWidth = barWidth
    )
}

@Composable
fun BarCanvas(barList: List<Bar>) {

    var visibleBarsCount by remember { mutableIntStateOf(50) }
    var barWidth by remember { mutableFloatStateOf(0f) }
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var scrolledBy by remember { mutableFloatStateOf(0f) }

    val transformableState = TransformableState { zoomChange, panChange, _ ->
        visibleBarsCount = (visibleBarsCount / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_COUNT, barList.size)
        scrolledBy = (scrolledBy + panChange.x)
            .coerceIn(0f, barWidth * barList.size - screenWidth)
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .transformable(transformableState)
    ) {
        val minY = 0f
        val maxY = size.height
        val minPrice = barList.minOf { it.min }
        val maxPrice = barList.maxOf { it.max }
        screenWidth = size.width
        barWidth = size.width / visibleBarsCount
        translate(left = scrolledBy) {
            barList.forEachIndexed { index, bar ->
                drawBar(minY, maxY, minPrice, maxPrice, index, bar, barWidth)
            }
        }
    }
}