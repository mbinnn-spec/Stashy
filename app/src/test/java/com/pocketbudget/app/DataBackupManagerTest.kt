package com.pocketbudget.app

import com.pocketbudget.app.data.DataBackupManager
import com.pocketbudget.app.data.local.entity.CategoryEntity
import com.pocketbudget.app.data.local.entity.GoalEntity
import com.pocketbudget.app.data.local.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DataBackupManagerTest {

    @Test
    fun testExportAndImportJsonRoundTrip() {
        val categories = listOf(
            CategoryEntity(
                id = "cat_food",
                name = "Food",
                type = "EXPENSE",
                iconName = "Restaurant",
                colorHex = "#F59E0B"
            )
        )

        val transactions = listOf(
            TransactionEntity(
                id = "tx_1",
                type = "EXPENSE",
                amount = 15000.0,
                categoryId = "cat_food",
                categoryName = "Food",
                note = "Snack",
                date = 1726880000000L
            )
        )

        val goals = listOf(
            GoalEntity(
                id = "goal_samsung",
                name = "Samsung A55",
                targetAmount = 4500000.0,
                currentAmount = 346000.0
            )
        )

        // Export
        val json = DataBackupManager.exportToJson(categories, transactions, goals)
        assertNotNull(json)

        // Import
        val backup = DataBackupManager.importFromJson(json)
        assertEquals(1, backup.categories.size)
        assertEquals("Food", backup.categories[0].name)

        assertEquals(1, backup.transactions.size)
        assertEquals(15000.0, backup.transactions[0].amount, 0.001)
        assertEquals("Snack", backup.transactions[0].note)

        assertEquals(1, backup.goals.size)
        assertEquals("Samsung A55", backup.goals[0].name)
        assertEquals(4500000.0, backup.goals[0].targetAmount, 0.001)
        assertEquals(346000.0, backup.goals[0].currentAmount, 0.001)
    }
}
