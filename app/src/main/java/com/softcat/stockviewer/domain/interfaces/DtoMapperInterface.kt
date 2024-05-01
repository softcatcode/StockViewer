package com.softcat.stockviewer.domain.interfaces

import com.softcat.stockviewer.data.models.BarDto
import com.softcat.stockviewer.domain.entities.Bar

interface DtoMapperInterface {

    fun mapBarDtoListToEntityList(list: List<BarDto>): List<Bar>
}