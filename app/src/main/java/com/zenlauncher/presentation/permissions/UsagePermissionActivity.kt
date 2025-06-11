package com.zenlauncher.presentation.permissions

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.zenlauncher.R
import com.zenlauncher.databinding.ActivityUsagePermissionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity para solicitar permissão de acesso às estatísticas de uso.
 */
@AndroidEntryPoint
class UsagePermissionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUsagePermissionBinding
    
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, UsagePermissionActivity::class.java)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsagePermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissionStatus()
    }
    
    override fun onResume() {
        super.onResume()
        // Verificar novamente quando o usuário voltar das configurações
        lifecycleScope.launch {
            delay(500) // Pequeno delay para garantir que as configurações foram aplicadas
            checkPermissionStatus()
        }
    }
    
    private fun setupUI() {
        binding.titleText.text = "Permissão Necessária"
        binding.descriptionText.text = 
            "O MindfulLauncher precisa de acesso às estatísticas de uso para monitorar " +
            "o tempo de tela e promover um uso mais consciente dos aplicativos."
        
        binding.benefitsText.text = 
            "Com esta permissão, você poderá:\n\n" +
            "• Receber avisos quando usar um app por muito tempo\n" +
            "• Ver estatísticas detalhadas de uso\n" +
            "• Configurar bloqueios automáticos por tempo\n" +
            "• Melhorar seu bem-estar digital"
        
        binding.grantPermissionButton.setOnClickListener {
            openUsageAccessSettings()
        }
        
        binding.skipButton.setOnClickListener {
            finishWithResult(false)
        }
        
        binding.closeButton.setOnClickListener {
            finish()
        }
    }
    
    private fun checkPermissionStatus() {
        if (hasUsageStatsPermission()) {
            binding.statusIcon.setImageResource(R.drawable.ic_check_circle)
            binding.statusIcon.setColorFilter(getColor(android.R.color.holo_green_dark))
            binding.statusText.text = "Permissão concedida!"
            binding.statusText.setTextColor(getColor(android.R.color.holo_green_dark))
            
            binding.grantPermissionButton.text = "Continuar"
            binding.grantPermissionButton.setOnClickListener {
                finishWithResult(true)
            }
            
            // Auto-fechar após 2 segundos
            lifecycleScope.launch {
                delay(2000)
                finishWithResult(true)
            }
        } else {
            binding.statusIcon.setImageResource(R.drawable.ic_warning)
            binding.statusIcon.setColorFilter(getColor(android.R.color.holo_orange_dark))
            binding.statusText.text = "Permissão necessária"
            binding.statusText.setTextColor(getColor(android.R.color.holo_orange_dark))
            
            binding.grantPermissionButton.text = "Conceder Permissão"
        }
    }
    
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    private fun openUsageAccessSettings() {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            
            Snackbar.make(
                binding.root,
                "Encontre o MindfulLauncher na lista e ative a permissão",
                Snackbar.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            // Fallback para configurações gerais se a intent específica falhar
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
                
                Snackbar.make(
                    binding.root,
                    "Vá em Privacidade > Acesso de uso especial > Acesso de estatísticas de uso",
                    Snackbar.LENGTH_LONG
                ).show()
            } catch (e2: Exception) {
                Snackbar.make(
                    binding.root,
                    "Não foi possível abrir as configurações automaticamente",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun finishWithResult(granted: Boolean) {
        val resultIntent = Intent().apply {
            putExtra("permission_granted", granted)
        }
        setResult(if (granted) RESULT_OK else RESULT_CANCELED, resultIntent)
        finish()
    }
}
