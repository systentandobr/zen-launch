#!/bin/bash

# Script 9 criado automaticamente
echo "Executando claude-ai9.sh"
cat << 'EOF' > android/app/src/main/java/com/zenlauncher/MinimalLauncherPackage.kt
package com.zenlauncher

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.ArrayList

/**
 * Pacote principal que registra todos os módulos nativos do Zen Launcher.
 * Esta classe é responsável por fornecer todos os módulos nativos para o React Native.
 */
class MinimalLauncherPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        val modules = ArrayList<NativeModule>()
        modules.add(AppUsageStatsModule(reactContext))
        modules.add(LauncherIntegrationModule(reactContext))
        modules.add(NotificationListenerModule(reactContext))
        modules.add(SystemSettingsModule(reactContext))
        modules.add(PackageEventsModule(reactContext))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
EOF