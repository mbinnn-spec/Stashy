# Room Database Proguard Rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep Domain & Entity Models
-keep class com.pocketbudget.app.domain.model.** { *; }
-keep class com.pocketbudget.app.data.local.entity.** { *; }
-keep class com.pocketbudget.app.data.BackupData { *; }
