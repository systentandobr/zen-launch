package com.zenlauncher.presentation.focus

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zenlauncher.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity para bloqueio após uso excessivo (2 horas)
 */
@AndroidEntryPoint
class UsageBlockActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_block)
        
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
            
        val hours = usageMinutes / 60
        val minutes = usageMinutes % 60
        
        findViewById<TextView>(R.id.blockMessage).text = 
            "Você usou este app por ${hours}h${if (minutes > 0) " ${minutes}min" else ""}.\n\n" +
            "Para seu bem-estar, o app foi bloqueado temporariamente."
            
        findViewById<TextView>(R.id.suggestionText).text =
            "Sugestões de atividades para diminuir a ansiedade e dopamina:\n\n" +
            "• Faça uma caminhada ao ar livre\n" +
            "• Leia um livro ou revista\n" +
            "• Toque um instrumento musical\n" +
            "• Ouça música relaxante\n" +
            "• Pratique meditação ou respiração\n" +
            "• Converse com amigos ou família\n" +
            "• Faça alongamentos ou exercícios"
            
        findViewById<Button>(R.id.okButton).setOnClickListener {
            goHome()
        }
    }
    
    private fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
    
    override fun onBackPressed() {
        // Impede que o usuário volte ao app bloqueado
        goHome()
    }
}
