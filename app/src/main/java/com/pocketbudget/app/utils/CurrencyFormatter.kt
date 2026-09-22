package com.pocketbudget.app.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {

    private val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val formatter = DecimalFormat("#,###", symbols)

    /**
     * Formats an amount to Indonesian Rupiah standard format:
     * e.g. 346000 -> "Rp 346.000"
     * e.g. 0 -> "Rp 0"
     * e.g. -15000 -> "-Rp 15.000"
     */
    fun formatRupiah(
        amount: Double,
        showPlusSign: Boolean = false,
        includeRpPrefix: Boolean = true
    ): String {
        val isNegative = amount < 0
        val absAmount = abs(amount).toLong()
        val formattedNumber = formatter.format(absAmount)

        val prefix = if (includeRpPrefix) "Rp " else ""

        return when {
            isNegative -> "-$prefix$formattedNumber"
            showPlusSign && amount > 0 -> "+$prefix$formattedNumber"
            else -> "$prefix$formattedNumber"
        }
    }

    fun formatRupiah(amount: Long): String {
        return formatRupiah(amount.toDouble())
    }

    /**
     * Formats plain digits as typed by user to dot-separated thousands:
     * e.g. "346000" -> "346.000"
     */
    fun formatRawDigits(rawDigits: String): String {
        val cleanDigits = rawDigits.filter { it.isDigit() }
        if (cleanDigits.isEmpty()) return ""
        val parsed = cleanDigits.toLongOrNull() ?: return cleanDigits
        return formatter.format(parsed)
    }

    /**
     * Parses formatted text or raw string into Double
     */
    fun parseAmount(input: String): Double {
        val cleanString = input.replace("Rp", "")
            .replace(".", "")
            .replace(" ", "")
            .trim()
        return cleanString.toDoubleOrNull() ?: 0.0
    }
}
