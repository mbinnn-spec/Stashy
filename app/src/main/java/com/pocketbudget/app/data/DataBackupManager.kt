package com.pocketbudget.app.data

import com.pocketbudget.app.data.local.entity.CategoryEntity
import com.pocketbudget.app.data.local.entity.GoalEntity
import com.pocketbudget.app.data.local.entity.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val version: Int,
    val exportedAt: Long,
    val categories: List<CategoryEntity>,
    val transactions: List<TransactionEntity>,
    val goals: List<GoalEntity>
)

object DataBackupManager {

    private const val BACKUP_VERSION = 1

    /**
     * Serializes all app data into a structured JSON string
     */
    fun exportToJson(
        categories: List<CategoryEntity>,
        transactions: List<TransactionEntity>,
        goals: List<GoalEntity>
    ): String {
        val root = JSONObject()
        root.put("version", BACKUP_VERSION)
        root.put("exportedAt", System.currentTimeMillis())

        // Categories
        val catArray = JSONArray()
        categories.forEach { cat ->
            val obj = JSONObject().apply {
                put("id", cat.id)
                put("name", cat.name)
                put("type", cat.type)
                put("iconName", cat.iconName)
                put("colorHex", cat.colorHex)
                put("isCustom", cat.isCustom)
            }
            catArray.put(obj)
        }
        root.put("categories", catArray)

        // Transactions
        val txArray = JSONArray()
        transactions.forEach { tx ->
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("type", tx.type)
                put("amount", tx.amount)
                put("categoryId", tx.categoryId)
                put("categoryName", tx.categoryName)
                put("note", tx.note ?: JSONObject.NULL)
                put("date", tx.date)
                put("createdAt", tx.createdAt)
            }
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        // Goals
        val goalArray = JSONArray()
        goals.forEach { g ->
            val obj = JSONObject().apply {
                put("id", g.id)
                put("name", g.name)
                put("targetAmount", g.targetAmount)
                put("currentAmount", g.currentAmount)
                put("deadline", g.deadline ?: JSONObject.NULL)
                put("createdAt", g.createdAt)
            }
            goalArray.put(obj)
        }
        root.put("goals", goalArray)

        return root.toString(2) // Pretty print with 2 indent spaces
    }

    /**
     * Parses and validates JSON string into BackupData
     */
    fun importFromJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val version = root.optInt("version", 1)
        val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())

        // Categories
        val categories = mutableListOf<CategoryEntity>()
        val catArray = root.optJSONArray("categories")
        if (catArray != null) {
            for (i in 0 until catArray.length()) {
                val obj = catArray.getJSONObject(i)
                categories.add(
                    CategoryEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        type = obj.getString("type"),
                        iconName = obj.optString("iconName", "Category"),
                        colorHex = obj.optString("colorHex", "#3B82F6"),
                        isCustom = obj.optBoolean("isCustom", false)
                    )
                )
            }
        }

        // Transactions
        val transactions = mutableListOf<TransactionEntity>()
        val txArray = root.optJSONArray("transactions")
        if (txArray != null) {
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = obj.getString("id"),
                        type = obj.getString("type"),
                        amount = obj.getDouble("amount"),
                        categoryId = obj.getString("categoryId"),
                        categoryName = obj.getString("categoryName"),
                        note = if (obj.isNull("note")) null else obj.getString("note"),
                        date = obj.getLong("date"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Goals
        val goals = mutableListOf<GoalEntity>()
        val goalArray = root.optJSONArray("goals")
        if (goalArray != null) {
            for (i in 0 until goalArray.length()) {
                val obj = goalArray.getJSONObject(i)
                goals.add(
                    GoalEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        targetAmount = obj.getDouble("targetAmount"),
                        currentAmount = obj.optDouble("currentAmount", 0.0),
                        deadline = if (obj.isNull("deadline")) null else obj.getLong("deadline"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        return BackupData(
            version = version,
            exportedAt = exportedAt,
            categories = categories,
            transactions = transactions,
            goals = goals
        )
    }
}
