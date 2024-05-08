package com.softcat.stockviewer.di

import com.softcat.stockviewer.presentation.stockPlot.StockViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::StockViewModel)
}