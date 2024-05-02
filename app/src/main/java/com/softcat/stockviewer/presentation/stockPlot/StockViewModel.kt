package com.softcat.stockviewer.presentation.stockPlot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcat.stockviewer.data.mappers.DtoMapper
import com.softcat.stockviewer.data.mappers.EnumMapper
import com.softcat.stockviewer.data.network.ApiFactory
import com.softcat.stockviewer.domain.entities.TimeFrame
import com.softcat.stockviewer.domain.interfaces.DtoMapperInterface
import com.softcat.stockviewer.domain.interfaces.EnumMapperInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StockViewModel: ViewModel() {

    private val apiService = ApiFactory.apiService
    private val dtoMapper: DtoMapperInterface = DtoMapper()
    private val enumMapper: EnumMapperInterface = EnumMapper()

    private val _state = MutableStateFlow<StockScreenState>(StockScreenState.Initial)
    val state = _state.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(this::class.qualifiedName, throwable.toString())
    }

    init {
        loadBars()
    }

    fun loadBars(timeFrame: TimeFrame = TimeFrame.MIN_30) {
        _state.value = StockScreenState.Loading
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val timeFrameData = enumMapper.mapTimeFrameToString(timeFrame)
            val barDtoResponse = apiService.loadBars(timeFrameData)
            val newBarList = dtoMapper.mapBarDtoListToEntityList(barDtoResponse.barList)
            _state.value = StockScreenState.Bars(newBarList, timeFrame)
        }
    }
}