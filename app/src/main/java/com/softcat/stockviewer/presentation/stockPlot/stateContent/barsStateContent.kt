package com.softcat.stockviewer.presentation.stockPlot.stateContent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcat.stockviewer.R
import com.softcat.stockviewer.domain.entities.Bar
import com.softcat.stockviewer.domain.entities.TimeFrame
import com.softcat.stockviewer.presentation.stockPlot.StockPlotState
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
    val currentPriceY = size.height - (current - min) * pixelsPerUnit
    val delta = 35

    drawInfoLine(y = 0f, textMeasurer = textMeasurer, text = max.toString())
    drawInfoLine(y = size.height, textMeasurer = textMeasurer, text = min.toString())
    drawInfoLine(
        y = currentPriceY,
        textMeasurer = textMeasurer,
        text = if (currentPriceY < delta || size.height - currentPriceY < delta) "" else current.toString()
    )
}

@Composable
fun TimeFrames(
    modifier: Modifier = Modifier,
    selected: TimeFrame,
    onElementClicked: (TimeFrame) -> Unit
) {
    val firstColor = MaterialTheme.colorScheme.background
    val secondColor = MaterialTheme.colorScheme.onBackground
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeFrame.entries.forEach {
            val labelResId = when (it) {
                TimeFrame.MIN_5 -> R.string.timeframe_5_minutes
                TimeFrame.MIN_15 -> R.string.timeframe_15_minutes
                TimeFrame.MIN_30 -> R.string.timeframe_30_minutes
                TimeFrame.HOUR_1 -> R.string.timeframe_1_hours
            }
            val textColor = if (selected == it) firstColor else secondColor
            val fieldColor = if (selected == it) secondColor else firstColor
            item {
                AssistChip(
                    modifier = Modifier.width(70.dp),
                    onClick = { onElementClicked(it) },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = stringResource(id = labelResId),
                            fontSize = 16.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors().copy(containerColor = fieldColor)
                )
            }
        }
    }

}

@Composable
fun BarCanvas(
    barList: List<Bar>,
    timeFrame: TimeFrame,
    onTimeFrameClicked: (TimeFrame) -> Unit
) {
    var stockPlotState by rememberSaveable { mutableStateOf(StockPlotState(barList)) }
    val textMeasurer = rememberTextMeasurer()

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TimeFrames(
            modifier = Modifier.fillMaxWidth(0.8f).height(40.dp),
            selected = timeFrame,
            onElementClicked = onTimeFrameClicked
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
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
}