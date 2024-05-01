package com.softcat.stockviewer.data.models

import com.google.gson.annotations.SerializedName

data class GetBarsResponseDto(
    @SerializedName("results") val barList: List<BarDto>
)