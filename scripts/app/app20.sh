#!/bin/bash

# Script 20 criado automaticamente
echo "Executando claude-ai20.sh"
cat << 'EOF' > android/app/src/main/java/com/zenlauncher/LauncherIntegrationModule.kt
package com.zenlauncher

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule
import java.io.ByteArrayOutputStream

@ReactModule(name = LauncherIntegrationModule.NAME)
class LauncherIntegrationModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        const val NAME = "LauncherIntegrationModule"
    }

    override fun getName(): String = NAME

    @ReactMethod
    fun getInstalledApps(promise: Promise) {
        try {
            val pm = reactApplicationContext.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            
            val apps = pm.queryIntentActivities(intent, 0)
            val result = WritableNativeArray()
            
            val processedPackages = HashSet<String>()
            
            for (resolveInfo in apps) {
                val packageName = resolveInfo.activityInfo.packageName
                
                // Evita duplicatas
                if (packageName in processedPackages) continue
                processedPackages.add(packageName)
                
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    
                    val entry = WritableNativeMap()
                    entry.putString("id", packageName)
                    entry.putString("name", appName)
                    entry.putString("packageName", packageName)
                    entry.putBoolean("isRestricted", false)
                    
                    result.pushMap(entry)
                } catch (e: Exception) {
                    // Ignora pacotes que não podem ser resolvidos
                }
            }
            
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun launchApp(packageName: String, promise: Promise) {
        try {
            val pm = reactApplicationContext.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                reactApplicationContext.startActivity(intent)
                promise.resolve(true)
            } else {
                promise.resolve(false)
            }
        } catch (e: Exception) {
            promise.reject("ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun getAppIcon(packageName: String, promise: Promise) {
        try {
            val pm = reactApplicationContext.packageManager
            val drawable = pm.getApplicationIcon(packageName)
            val base64Icon = drawableToBase64(drawable)
            promise.resolve(base64Icon)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun isAppRunning(packageName: String, promise: Promise) {
        // Esta função requer mais permissões para implementar corretamente
        // Por simplicidade, retornamos false
        promise.resolve(false)
    }

    private fun drawableToBase64(drawable: Drawable): String {
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    @ReactMethod
    fun setAsDefaultLauncher(promise: Promise) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            reactApplicationContext.startActivity(intent)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message, e)
        }
    }
}
EOF