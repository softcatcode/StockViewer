package com.softcat.stockviewer.data.mappers

import com.softcat.stockviewer.domain.entities.TimeFrame
import com.softcat.stockviewer.domain.interfaces.EnumMapperInterface

class EnumMapper: EnumMapperInterface {
    override fun mapTimeFrameToString(timeFrame: TimeFrame) = when (timeFrame) {
        TimeFrame.MIN_5 -> "5/minute"
        TimeFrame.MIN_15 -> "15/minute"
        TimeFrame.MIN_30 -> "30/minute"
        TimeFrame.HOUR_1 -> "1/hour"
    }
}