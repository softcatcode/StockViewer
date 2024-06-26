package com.softcat.stockviewer.data.network

import com.softcat.stockviewer.data.models.GetBarsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("https://api.polygon.io/v2/aggs/ticker/AAPL/range/{timeframe}/2022-01-09/2023-01-09?adjusted=true&sort=desc&apiKey=LYQMRtnPnsiU8hm3vD_szOzby6cXFEBv")
    suspend fun loadBars(
        @Path("timeframe") timeFrame: String,
        @Query("limit") limit: Int
    ): GetBarsResponseDto
}