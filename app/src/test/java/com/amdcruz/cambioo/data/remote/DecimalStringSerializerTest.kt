package com.amdcruz.cambioo.data.remote

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class DecimalStringSerializerTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun deserialize_jsonNumber_keepsDecimalString() {
        val payload = """
            {"date":"2026-10-30","base":"BRL","quote":"USD","rate":0.18408}
        """.trimIndent()
        val actual = json.decodeFromString(FrankfurterRateDto.serializer(), payload)
        assertEquals("0.18408", actual.rate)
        assertEquals("USD", actual.quote)
    }

    @Test
    fun deserialize_jsonString_keepsDecimalString() {
        val payload = """
            {"date":"2026-10-30","base":"BRL","quote":"USD","rate":"0.18408"}
        """.trimIndent()
        val actual = json.decodeFromString(FrankfurterRateDto.serializer(), payload)
        assertEquals("0.18408", actual.rate)
    }
}
