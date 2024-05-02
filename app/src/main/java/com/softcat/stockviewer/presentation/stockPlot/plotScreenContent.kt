package com.softcat.stockviewer.presentation.stockPlot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

fun DrawScope.drawInfoLine(
    y: Float,
    startX: Float = 0f,
    endX: Float = size.width,
    color: Color = Color.White,
    strokeWidth: Float = 1f,
    textMeasurer: TextMeasurer? = null,
    text: String = ""
) {
    textMeasurer?.apply {
        val textLayoutResult = measure(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                color = color
            )
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(size.width - textLayoutResult.size.width,y - textLayoutResult.size.height)
        )
    }
    drawLine(
        start = Offset(startX, y),
        end = Offset(endX, y),
        color = color,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )
}

fun DrawScope.drawInfoLines(
    min: Float,
    max: Float,
    current: Float,
    textMeasurer: TextMeasurer
) {
    val pixelsPerUnit = size.height / (max - min)
    drawInfoLine(y = 0f, textMeasurer = textMeasurer, text = max.toString())
    drawInfoLine(
        y = size.height - (current - min) * pixelsPerUnit,
        textMeasurer = textMeasurer,
        text = current.toString()
    )
    drawInfoLine(y = size.height, textMeasurer = textMeasurer, text = min.toString())
}

@Composable
fun BarCanvas(barList: List<Bar>) {
    var stockPlotState by rememberSaveable { mutableStateOf(StockPlotState(barList)) }
    val textMeasurer = rememberTextMeasurer()

    val transformableState = TransformableState { zoomChange, panChange, _ ->
        val visibleBarCount = (stockPlotState.visibleBarCount / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_COUNT, barList.size)
        val scrolledBy = (stockPlotState.scrolledBy + panChange.x)
            .coerceIn(0f, stockPlotState.barWidth * barList.size - stockPlotState.screenWidth)
        stockPlotState = stockPlotState.copy(
            visibleBarCount = visibleBarCount,
            scrolledBy = scrolledBy
        )
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 15.dp, bottom = 15.dp)
            .transformable(transformableState)
            .onSizeChanged {
                stockPlotState = stockPlotState.copy(screenWidth = it.width.toFloat())
            }
    ) {
        val minY = 0f
        val maxY = size.height
        val minPrice = stockPlotState.visibleBars.minOf { it.min }
        val maxPrice = stockPlotState.visibleBars.maxOf { it.max }
        translate(left = stockPlotState.scrolledBy) {
            barList.forEachIndexed { index, bar ->
                drawBar(minY, maxY, minPrice, maxPrice, index, bar, stockPlotState.barWidth)
            }
        }
        drawInfoLines(
            min = minPrice,
            max = maxPrice,
            current = stockPlotState.barList[0].close,
            textMeasurer = textMeasurer
        )
    }
}