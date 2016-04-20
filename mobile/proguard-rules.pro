# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/cjones/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-dontskipnonpubliclibraryclasses
-dontobfuscate
#-forceprocessing
#-optimizationpasses 5
#
#-keep class * extends android.app.Activity
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class .R
-keep class **.R$* {
    <fields>;
}
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keep public class * extends android.support.v7.preference.Preference
-keep class com.google.android.gms.common.api.** {*;}
-keep class com.google.android.gms.wearable.** {*;}
-keep class com.google.android.wearable.** {*;}

# Firefox
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepattributes SourceFile,LineNumberTable
# Preserve all fundamental application classes.

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.**

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.malinskiy.superrecyclerview.SwipeDismissRecyclerViewTouchListener*
-dontwarn uk.co.senab.photoview.**
-dontwarn com.google.**
-dontwarn android.support.**
