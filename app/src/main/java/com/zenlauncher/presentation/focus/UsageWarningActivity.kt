package com.zenlauncher.presentation.focus

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zenlauncher.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

/**
 * Activity para mostrar aviso de uso prolongado (1 hora)
 */
@AndroidEntryPoint
class UsageWarningActivity : AppCompatActivity() {
    
    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_warning)
        
        val packageName = intent.getStringExtra("PACKAGE_NAME") ?: ""
        val usageMinutes = intent.getLongExtra("USAGE_MINUTES", 0)
        
        setupUI(packageName, usageMinutes)
    }
    
    private fun setupUI(packageName: String, usageMinutes: Long) {
        val appInfo = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        
        findViewById<ImageView>(R.id.appIcon).setImageDrawable(
            appInfo?.loadIcon(packageManager)
        )
        
        findViewById<TextView>(R.id.appName).text = 
            appInfo?.loadLabel(packageManager) ?: packageName
            
        findViewById<TextView>(R.id.warningMessage).text = 
            "Você está usando este app há ${usageMinutes} minutos.\n\n" +
            "Que tal fazer uma pausa para cuidar da sua saúde e bem-estar?"
            
        findViewById<Button>(R.id.stopNowButton).setOnClickListener {
            stopApp()
        }
        
        findViewById<Button>(R.id.continueButton10).setOnClickListener {
            continueUsing(10)
        }
        
        findViewById<Button>(R.id.continueButton20).setOnClickListener {
            continueUsing(20)
        }
        
        findViewById<Button>(R.id.continueButton30).setOnClickListener {
            continueUsing(30)
        }
    }
    
    private fun stopApp() {
        // Volta para a home
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
    
    private fun continueUsing(minutes: Int) {
        // Registra que o usuário escolheu continuar
        // TODO: Implementar lógica para adiar próximo aviso
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}
