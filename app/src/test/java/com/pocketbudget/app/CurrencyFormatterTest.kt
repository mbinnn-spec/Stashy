package com.pocketbudget.app

import com.pocketbudget.app.utils.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testFormatRupiahPositive() {
        val result = CurrencyFormatter.formatRupiah(346000.0)
        assertEquals("Rp 346.000", result)
    }

    @Test
    fun testFormatRupiahZero() {
        val result = CurrencyFormatter.formatRupiah(0.0)
        assertEquals("Rp 0", result)
    }

    @Test
    fun testFormatRupiahNegative() {
        val result = CurrencyFormatter.formatRupiah(-15000.0)
        assertEquals("-Rp 15.000", result)
    }

    @Test
    fun testFormatRawDigits() {
        val result = CurrencyFormatter.formatRawDigits("346000")
        assertEquals("346.000", result)
    }

    @Test
    fun testParseAmount() {
        val parsed = CurrencyFormatter.parseAmount("Rp 346.000")
        assertEquals(346000.0, parsed, 0.001)
    }
}
