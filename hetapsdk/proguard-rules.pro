# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#Could not instantiate mapper : com.thoughtworks.xstream.mapper.EnumMapper :
# <init> [interface com.thoughtworks.xstream.mapper.Mapper
-dontwarn com.google.gson.stream.**
-dontwarn com.google.gson.**
-keepattributes *Annotation*#使用注解需要添加
-keep class android.os.** { *; }
-keep class android.net.wifi.** { *; }
-keep class android.text.** { *; }
-keep class java.lang.** { *; }
-keep class com.google.gson.** {*;}


-keep public class com.het.ap.HeTApApi { *; }
-keep public class com.het.ap.callback.** { *; }
-keep public class com.het.ap.bean.** { *; }






