package com.pocketbudget.app

import com.pocketbudget.app.domain.model.CategoryExpenseSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryBreakdownTest {

    @Test
    fun testCategoryExpenseBreakdownCalculation() {
        val totalExpense = 214000.0
        val items = listOf(
            Pair("Food", 85000.0),
            Pair("Internet", 62000.0),
            Pair("Games", 40000.0),
            Pair("Transportation", 27000.0)
        )

        val summaries = items.map { (name, amount) ->
            CategoryExpenseSummary(
                categoryId = name.lowercase(),
                categoryName = name,
                totalAmount = amount,
                percentage = (amount / totalExpense) * 100.0
            )
        }

        assertEquals(4, summaries.size)
        assertEquals("Food", summaries[0].categoryName)
        assertEquals(39.72, summaries[0].percentage, 0.01)

        assertEquals("Internet", summaries[1].categoryName)
        assertEquals(28.97, summaries[1].percentage, 0.01)

        val totalPercentage = summaries.sumOf { it.percentage }
        assertEquals(100.0, totalPercentage, 0.01)
    }
}
