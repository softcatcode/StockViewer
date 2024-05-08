package com.softcat.stockviewer.presentation.stockPlot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcat.stockviewer.data.network.ApiService
import com.softcat.stockviewer.domain.entities.TimeFrame
import com.softcat.stockviewer.domain.interfaces.DtoMapperInterface
import com.softcat.stockviewer.domain.interfaces.EnumMapperInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StockViewModel(
    private val apiService: ApiService,
    private val dtoMapper: DtoMapperInterface,
    private val enumMapper: EnumMapperInterface
): ViewModel() {

    private var lastState: StockScreenState = StockScreenState.Initial

    private val _state = MutableStateFlow<StockScreenState>(StockScreenState.Initial)
    val state = _state.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.value = lastState
        Log.d(this::class.qualifiedName, throwable.toString())
    }

    init {
        loadBars()
    }

    fun loadBars(timeFrame: TimeFrame = TimeFrame.MIN_30) {
        lastState = _state.value
        _state.value = StockScreenState.Loading
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val timeFrameData = enumMapper.mapTimeFrameToString(timeFrame)
            val barDtoResponse = apiService.loadBars(timeFrameData, limit = 5000)
            val newBarList = dtoMapper.mapBarDtoListToEntityList(barDtoResponse.barList)
            _state.value = StockScreenState.Bars(newBarList, timeFrame)
        }
    }
}