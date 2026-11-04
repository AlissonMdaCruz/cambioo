package com.amdcruz.cambioo.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApi {
    @GET("v2/providers/ecb/rates")
    suspend fun fetchWindowRates(
        @Query("base") base: String = "brl",
        @Query("quotes") quotes: String = "usd,eur,gbp,chf",
        @Query("from") from: String,
        @Query("to") to: String,
    ): List<FrankfurterRateDto>
}
