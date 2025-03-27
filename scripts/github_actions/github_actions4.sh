#!/bin/bash

# Script 4 criado automaticamente
echo "Executando claude-ai4.sh"
cat << 'EOF' > android/app/proguard-rules.pro
# Regras ProGuard para o Zen Launcher

# Mantenha introspeção de modelos do React Native
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters

# Não faça strip de qualquer método anotado com @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
}

-keep @com.facebook.react.bridge.ReactModule class *

# Mantenha componentes nativos
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

# Preserve anotações do React Native
-keep class com.facebook.react.bridge.ReadableType

# Hermes
-keep class com.facebook.hermes.unicode.** { *; }
-keep class com.facebook.jni.** { *; }

# Mantenha nossos módulos nativos específicos do launcher
-keep class com.zenlauncher.** { *; }
-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }

# OkHttp e outros componentes de rede
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Para UsageStatsManager
-keep class android.app.usage.** { *; }

# Para NotificationListenerService
-keep class android.service.notification.** { *; }

# Para LauncherApps
-keep class android.content.pm.LauncherApps { *; }

# Regras específicas para Fast Image
-keep class com.dylanvann.fastimage.** { *; }

# Regras para o React Native Reanimated
-keep class com.swmansion.reanimated.** { *; }
-keep class com.facebook.react.turbomodule.** { *; }

# Evite que classes de interface de serviço sejam removidas
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Mantenha as Activity que serão chamadas pelo sistema
-keepclasseswithmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# ReactNative NativeModules
-keep class * extends com.facebook.react.bridge.ReactContextBaseJavaModule { *; }
EOF