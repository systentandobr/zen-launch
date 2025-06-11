package com.zenlauncher.presentation.common.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppInfoParcelable

/**
 * Extensões para carregar ícones de aplicativos
 */

/**
 * Carrega o ícone de um aplicativo usando o PackageManager
 */
fun AppInfoParcelable.loadIcon(context: Context): Drawable {
    return try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon)
            ?: context.getDrawable(android.R.drawable.sym_def_app_icon)!!
    }
}
