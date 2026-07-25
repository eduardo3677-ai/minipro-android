# Proguard rules for FlashLabs
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.echosmart.flashlabs.** { *; }
