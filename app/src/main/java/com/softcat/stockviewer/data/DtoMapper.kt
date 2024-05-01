package com.softcat.stockviewer.data

import com.softcat.stockviewer.data.models.BarDto
import com.softcat.stockviewer.domain.entities.Bar
import com.softcat.stockviewer.domain.interfaces.DtoMapperInterface

class DtoMapper: DtoMapperInterface {
    override fun mapBarDtoListToEntityList(list: List<BarDto>) = list.map {
        Bar(
            open = it.open,
            close = it.close,
            min = it.min,
            max = it.max,
            time = it.time
        )
    }

}