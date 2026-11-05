package com.amdcruz.cambioo.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FrankfurterRateDto(
    val date: String,
    val base: String,
    val quote: String,
    @Serializable(with = DecimalStringSerializer::class)
    val rate: String,
)

@Serializable
data class FrankfurterErrorDto(
    val status: Int? = null,
    val message: String? = null,
)
