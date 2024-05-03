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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

private const val MIN_VISIBLE_COUNT = 10

fun DrawScope.drawBar(
    minY: Float, maxY: Float,
    minPrice: Float, maxPrice: Float,
    offsetX: Float,
    bar: Bar,
    barWidth: Float
) {
    val pixelsPerPrice = (maxY - minY) / (maxPrice - minPrice)
    val minPoint = Offset(offsetX, maxY - (bar.min - minPrice) * pixelsPerPrice)
    val maxPoint = Offset(offsetX, maxY - (bar.max - minPrice) * pixelsPerPrice)
    val openPoint = Offset(offsetX, maxY - (bar.open - minPrice) * pixelsPerPrice)
    val closePoint = Offset(offsetX, maxY - (bar.close - minPrice) * pixelsPerPrice)
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

fun DrawScope.drawTimeLine(
    textMeasurer: TextMeasurer,
    timeFrame: TimeFrame,
    offsetX: Float,
    bar: Bar,
    nextBar: Bar? = null
) {
    val calendar = bar.calendar
    val minute = calendar.get(Calendar.MINUTE)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val timeLineVisible = when (timeFrame) {
        TimeFrame.MIN_5 -> minute == 0
        TimeFrame.MIN_15 -> minute == 0 && hour % 2 == 0
        else -> day != nextBar?.calendar?.get(Calendar.DAY_OF_MONTH)
    }
    if (!timeLineVisible)
        return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        strokeWidth = 1f,
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )
    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val timeLabel = when (timeFrame) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> String.format("%02d:00", hour)
        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> String.format("%s %s", day, monthName)
    }
    val textLayoutResult = textMeasurer.measure(
        text = timeLabel,
        style = TextStyle(
            fontSize = 12.sp,
            color = Color.White
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(
            x = offsetX - textLayoutResult.size.width / 2,
            y = size.height - textLayoutResult.size.height
        )
    )
}

@Composable
fun BarCanvas(
    barList: List<Bar>,
    modifier: Modifier = Modifier,
    timeFrame: TimeFrame,
) {
    val textMeasurer = rememberTextMeasurer()
    val state = rememberSaveable { mutableStateOf(StockPlotState(barList)) }
    val currentPlotState = state.value

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarCount = (currentPlotState.visibleBarCount / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_COUNT, currentPlotState.barList.size)
        val scrolledBy = (currentPlotState.scrolledBy + panChange.x)
            .coerceIn(0f, max(currentPlotState.barWidth * currentPlotState.barList.size - state.value.screenWidth, 0f))
        state.value = currentPlotState.copy(
            visibleBarCount = visibleBarCount,
            scrolledBy = scrolledBy
        )
    }

    Canvas(
        modifier = modifier.transformable(transformableState).onSizeChanged {
            state.value = state.value.copy(screenWidth = it.width.toFloat())
        }
    ) {
        val minY = 0f
        val maxY = size.height
        val minPrice = currentPlotState.visibleBars.minOf { it.min }
        val maxPrice = currentPlotState.visibleBars.maxOf { it.max }
        translate(left = currentPlotState.scrolledBy) {
            currentPlotState.barList.forEachIndexed { index, bar ->
                val offsetX = size.width - currentPlotState.barWidth * (index + 0.5f)
                drawBar(minY, maxY, minPrice, maxPrice, offsetX, bar, currentPlotState.barWidth)
                drawTimeLine(
                    textMeasurer = textMeasurer,
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    bar = bar,
                    nextBar = if (index < currentPlotState.barList.lastIndex) currentPlotState.barList[index + 1] else null
                )
            }
        }
        drawInfoLines(
            min = minPrice,
            max = maxPrice,
            current = currentPlotState.barList[0].close,
            textMeasurer = textMeasurer
        )
    }
}

@Composable
fun BarPlot(
    barList: List<Bar>,
    timeFrame: TimeFrame,
    onTimeFrameClicked: (TimeFrame) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TimeFrames(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp),
            selected = timeFrame,
            onElementClicked = onTimeFrameClicked
        )
        BarCanvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .padding(top = 15.dp, bottom = 15.dp),
            barList = barList,
            timeFrame = timeFrame
        )
    }
}