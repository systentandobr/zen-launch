# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Mantenha nomes de arquivos e números de linha para rastreamento de erros
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Regras para o Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Regras para o Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.InstallIn *;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <methods>;
}

# Regras para Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Regras para classes do Android usadas pelo sistema
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# Regras específicas para o MindfulLauncher
-keep class com.zenlauncher.domain.entities.** { *; }
-keep class com.zenlauncher.data.datasources.system.** { *; }
-keep class com.zenlauncher.data.receivers.** { *; }
-keep class com.zenlauncher.data.services.** { *; }

# Regras para manter objetos serializados
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}