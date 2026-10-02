# BoxManager — R8 / ProGuard (release play)
# Obiettivo Play: ≥25% obfuscation / shrinking / optimization (DEX), feb 2027.
# Fonte: B-PLAY-DEX-R8 + Play Console technical quality (DEX code optimization).

# Stack traces utili in Crashlytics / Play Vitals
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Kotlin
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

# Room (entità + DAO generati)
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Parcelable / enum usati da Intent / Bundle
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# View Binding / layout XML (se referenziati per nome)
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# ML Kit barcode + OCR testo
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.internal.mlkit_** { *; }
-dontwarn com.google.android.gms.internal.mlkit_**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ZXing (QR generazione)
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# JSON org.json (già in Android SDK; keep se usiamo reflection indiretta)
-dontwarn org.json.**
