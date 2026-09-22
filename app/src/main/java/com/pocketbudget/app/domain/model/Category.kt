package com.pocketbudget.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean = false
) {
    companion object {
        val defaultIncomeCategories = listOf(
            Category(id = "inc_allowance", name = "Allowance", type = TransactionType.INCOME, iconName = "AccountBalance", colorHex = "#529B78"),
            Category(id = "inc_salary", name = "Salary", type = TransactionType.INCOME, iconName = "Payments", colorHex = "#4A8C6D"),
            Category(id = "inc_gift", name = "Gift", type = TransactionType.INCOME, iconName = "CardGiftcard", colorHex = "#5B82A6"),
            Category(id = "inc_other", name = "Other", type = TransactionType.INCOME, iconName = "MoreHoriz", colorHex = "#6A7B8C")
        )

        val defaultExpenseCategories = listOf(
            Category(id = "exp_food", name = "Food", type = TransactionType.EXPENSE, iconName = "Restaurant", colorHex = "#B88458"),
            Category(id = "exp_transport", name = "Transportation", type = TransactionType.EXPENSE, iconName = "DirectionsCar", colorHex = "#5B82A6"),
            Category(id = "exp_internet", name = "Internet", type = TransactionType.EXPENSE, iconName = "Wifi", colorHex = "#5A8B9C"),
            Category(id = "exp_games", name = "Games", type = TransactionType.EXPENSE, iconName = "SportsEsports", colorHex = "#787299"),
            Category(id = "exp_shopping", name = "Shopping", type = TransactionType.EXPENSE, iconName = "ShoppingBag", colorHex = "#A66E7A"),
            Category(id = "exp_school", name = "School", type = TransactionType.EXPENSE, iconName = "School", colorHex = "#5C8E75"),
            Category(id = "exp_entertainment", name = "Entertainment", type = TransactionType.EXPENSE, iconName = "Movie", colorHex = "#A86D6D"),
            Category(id = "exp_other", name = "Other", type = TransactionType.EXPENSE, iconName = "MoreHoriz", colorHex = "#6A7B8C")
        )

        val allDefaults = defaultIncomeCategories + defaultExpenseCategories
    }
}
