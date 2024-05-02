package com.softcat.stockviewer.presentation.stockPlot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcat.stockviewer.data.DtoMapper
import com.softcat.stockviewer.data.network.ApiFactory
import com.softcat.stockviewer.domain.interfaces.DtoMapperInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StockViewModel: ViewModel() {

    private val apiService = ApiFactory.apiService
    private val mapper: DtoMapperInterface = DtoMapper()

    private val _state = MutableStateFlow<StockScreenState>(StockScreenState.Initial)
    val state = _state.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(this::class.qualifiedName, throwable.toString())
    }

    init {
        loadBars()
    }

    private fun loadBars() {
        _state.value = StockScreenState.Loading
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val barDtoList = apiService.loadBars().barList
            _state.value = StockScreenState.Bars(mapper.mapBarDtoListToEntityList(barDtoList))
        }
    }
}