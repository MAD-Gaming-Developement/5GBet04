# Our default Pro-guard rules for all projects
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled


# Below lines for AF Integration only
-keep public class com.android.installreferrer.** { *; }

-keepclassmembers class com.ftmouse5g.slotmv4.jsBridge {
    public *;
}