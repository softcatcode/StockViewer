package com.softcat.stockviewer.data.models

import com.google.gson.annotations.SerializedName

data class BarDto(
    @SerializedName("o") val open: Float,
    @SerializedName("c") val close: Float,
    @SerializedName("l") val min: Float,
    @SerializedName("h") val max: Float,
    @SerializedName("t") val time: Long
)
