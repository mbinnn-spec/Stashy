# Stashy 🪙

**Stashy** is a modern, 100% offline, privacy-first personal finance and savings goal tracker for Android. Built with Jetpack Compose and Material 3, designed with an eye-friendly Nordic Slate palette.

---

## ✨ Features

- **📊 Smart Financial Dashboard**: Automatic calculation of Total Balance, Monthly Income, Monthly Expense, and Net Cashflow without manual ledger balancing.
- **⚡ Fast Transaction Logging**: Fluid numeric input with Indonesian Rupiah (`Rp`) currency formatting, customizable categories, and search & filtering.
- **🎯 Savings Goals**: Track progress toward targets with automated completion estimations based on weekly savings history.
- **🛡️ 100% Offline & Private**: Zero internet permissions requested (`INTERNET` permission not in manifest). Your data stays strictly on your device.
- **💾 JSON Backup & Restore**: Easily export your entire transaction and goal history to JSON and restore anytime via Android Storage Access Framework.
- **🌙 Calm Nordic Slate Theme**: Eye-friendly dark mode using muted slate blue, calm sage green, and warm bronze tones.

---

## 🛠️ Tech Stack

- **UI**: 100% Jetpack Compose with Material 3 (BOM 2024.09.00)
- **Architecture**: Clean Architecture / MVVM with Kotlin Coroutines & StateFlow
- **Database**: Room Database 2.6.1 with KSP code generator
- **Navigation**: Navigation Compose 2.8.0
- **Serialization**: Kotlinx Serialization (JSON)
- **Build System**: Gradle 8.14.3, Kotlin 2.0.20, Android Gradle Plugin 8.5.2
- **Compatibility**: minSdk 26 (Android 8.0 Oreo) - targetSdk 34 (Android 14)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- Android SDK 34

### Building from Source

```bash
# Clone the repository
git clone https://github.com/<your-username>/Stashy.git
cd Stashy

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
