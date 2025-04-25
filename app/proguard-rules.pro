# --- GENERAL RULES ---

# Preserve class and method names used in XML (like onClick methods)
-keepclassmembers class * {
    public void *(android.view.View);
}

# Keep annotations (required for modern Android SDKs)
-keepattributes *Annotation*

# Keep line numbers for better crash logs
-keepattributes SourceFile,LineNumberTable

# Keep Application class (if any custom one is used)
# -keep class com.satnamsinghmaggo.paathapp.MyApplication { *; }

# --- LOTTIE (Animation) ---
-keep class com.airbnb.lottie.** { *; }

# --- GSON (JSON Parsing) ---
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- VOLLEY (Networking) ---
-keep class com.android.volley.** { *; }

# --- OKHTTP3 (Networking) ---
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# --- JSOUP (HTML Parsing) ---
-dontwarn org.jsoup.**
-keep class org.jsoup.** { *; }

# --- ANDROIDX (Jetpack Components) ---
-keep class androidx.** { *; }
-dontwarn androidx.**

# --- FILE PROVIDER ---
-keep public class androidx.core.content.FileProvider {
    public *;
}

# --- OPTIONAL DEBUGGING RULES ---
# Uncomment if you want to keep method/field names for debugging
# -keepnames class * {
#     *;
# }

# --- TESTING CLASSES (optional) ---
# If you use any test-specific classes, you can exclude them like:
# -dontwarn junit.**

