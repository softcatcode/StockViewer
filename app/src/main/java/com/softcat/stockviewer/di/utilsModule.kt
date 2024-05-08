package com.softcat.stockviewer.di

import com.softcat.stockviewer.data.mappers.DtoMapper
import com.softcat.stockviewer.data.mappers.EnumMapper
import com.softcat.stockviewer.data.network.ApiFactory
import com.softcat.stockviewer.domain.interfaces.DtoMapperInterface
import com.softcat.stockviewer.domain.interfaces.EnumMapperInterface
import org.koin.dsl.module

val utilsModule = module {
    single { ApiFactory.apiService }
    single<DtoMapperInterface> { DtoMapper() }
    single<EnumMapperInterface> { EnumMapper() }
}