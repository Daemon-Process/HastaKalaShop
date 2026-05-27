# 🏺 Hasta-Kala Shop — Android App

**MindMatrix VTU Internship Program | Project Title 14**

A Micro-Sales Analytics & Billing tool for self-employed artisans.

---

## 📱 Features

| Screen | Description |
|---|---|
| **Login** | Shop name + artisan name setup. Displays product showcase. |
| **Dashboard** | Pie chart of best sellers, revenue summary, low-stock alerts |
| **Quick Bill** | Grid of all products (tap to record a sale instantly) |
| **Stock Overview** | All items sorted by lowest stock first with status indicators |
| **Income Log** | Bar chart + sale history filtered by This Week / This Month |

---

## 🛠️ Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- Min device: Android 7.0 (API 24)

### Steps

1. **Extract the zip** → Open the `HastaKalaShop` folder in Android Studio
   - `File → Open → select the HastaKalaShop folder`

2. **Sync Gradle**
   - Click **"Sync Now"** when prompted, or go to `File → Sync Project with Gradle Files`
   - Internet required to download: Room, MPAndroidChart, Material Components

3. **Run the app**
   - Connect a device or start an emulator (API 24+)
   - Click the **▶ Run** button

4. **First launch**
   - You'll see the **Login screen** with product category cards
   - Enter your name, shop name, and any password (min 4 chars)
   - Default products are auto-loaded on first install

---

## 🏗️ Architecture

```
com.hastakala.shop
├── model/          → Product, Sale data classes
├── data/
│   ├── db/         → Room Database, DAOs
│   └── repository/ → ShopRepository
├── utils/          → ShopViewModel, SessionManager
└── ui/
    ├── login/      → LoginActivity
    ├── dashboard/  → MainActivity (home + pie chart)
    ├── billing/    → BillingActivity (quick sale grid)
    ├── stock/      → StockActivity (inventory list)
    └── income/     → IncomeActivity (bar chart + log)
```

## 📦 Dependencies

- **Room 2.6** — Local SQLite database
- **MPAndroidChart 3.1** — Pie & Bar charts
- **Material Components 1.11** — UI widgets
- **ViewModel + LiveData** — Reactive UI
- **Kotlin Coroutines** — Async database ops
- **ViewBinding** — Type-safe view access

---

## 🌿 Product Categories (Pre-loaded)

| Category | Items |
|---|---|
| 👜 Bags | Banana Fiber Bag (Brown, Blue, Red, Green) |
| 🧺 Baskets | Woven Basket (Natural, Maroon) |
| 🔑 Keychains | Keychain (Yellow, Pink, Purple, Orange) |
| 👝 Pouches | Fiber Pouch (Beige, Blue) |
| 💛 Jewellery | Craft Earrings (Gold, Silver) |
| 🪵 Coasters | Coaster Set (Natural, Dark Brown) |

---

*Built for MindMatrix VTU Internship Program — Hasta-Kala Shop*
