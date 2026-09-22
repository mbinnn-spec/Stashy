package com.pocketbudget.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pocketbudget.app.domain.model.Category
import com.pocketbudget.app.domain.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "INCOME" or "EXPENSE"
    @ColumnInfo(name = "icon_name")
    val iconName: String,
    @ColumnInfo(name = "color_hex")
    val colorHex: String,
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        type = TransactionType.valueOf(type),
        iconName = iconName,
        colorHex = colorHex,
        isCustom = isCustom
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            type = category.type.name,
            iconName = category.iconName,
            colorHex = category.colorHex,
            isCustom = category.isCustom
        )
    }
}
