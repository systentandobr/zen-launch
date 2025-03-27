#!/bin/bash

# Script 7 criado automaticamente
echo "Executando claude-ai7.sh"
cat << 'EOF' > android/app/src/main/java/com/zenlauncher/PackageChangedReceiver.kt
package com.zenlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * Receptor de broadcasts para monitorar mudanças nos pacotes instalados.
 * Isso permite que o launcher atualize dinamicamente quando aplicativos são 
 * instalados, desinstalados ou atualizados.
 */
class PackageChangedReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "PackageChangedReceiver"
        private var reactContext: ReactApplicationContext? = null

        fun setReactContext(context: ReactApplicationContext) {
            reactContext = context
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val data = intent.data as Uri? ?: return
        val packageName = data.schemeSpecificPart ?: return

        Log.d(TAG, "Package changed: $action for $packageName")

        val pm = context.packageManager
        val eventName: String
        val params: WritableMap = Arguments.createMap()

        params.putString("packageName", packageName)

        when (action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                eventName = "packageAdded"
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    params.putString("appName", appName)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(TAG, "Error getting app info", e)
                }
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                eventName = "packageRemoved"
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                eventName = "packageUpdated"
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    params.putString("appName", appName)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(TAG, "Error getting app info", e)
                }
            }
            else -> return
        }

        // Notifica o React Native sobre a mudança no pacote
        reactContext?.let { ctx ->
            if (ctx.hasActiveCatalystInstance()) {
                ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit(eventName, params)
            }
        }
    }
}
EOF