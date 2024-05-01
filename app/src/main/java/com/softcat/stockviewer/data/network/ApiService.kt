package com.softcat.stockviewer.data.network

import com.softcat.stockviewer.data.models.GetBarsResponseDto
import retrofit2.http.GET

interface ApiService {

    @GET("https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/hour/2022-01-09/2023-01-09?adjusted=true&sort=asc&limit=50000&apiKey=LYQMRtnPnsiU8hm3vD_szOzby6cXFEBv")
    suspend fun loadBars(): GetBarsResponseDto
}