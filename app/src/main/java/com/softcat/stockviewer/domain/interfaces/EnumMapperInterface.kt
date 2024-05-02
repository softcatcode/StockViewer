package com.softcat.stockviewer.domain.interfaces

import com.softcat.stockviewer.domain.entities.TimeFrame

interface EnumMapperInterface {

    fun mapTimeFrameToString(timeFrame: TimeFrame): String
}