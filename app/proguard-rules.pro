# Add project specific ProGuard rules here.
-keep class com.hastakala.shop.model.** { *; }
-keep class com.hastakala.shop.data.db.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
