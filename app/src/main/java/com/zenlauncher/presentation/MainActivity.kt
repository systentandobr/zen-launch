package com.zenlauncher.presentation

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zenlauncher.R
import com.zenlauncher.data.receivers.PowerConnectionReceiver
import com.zenlauncher.data.services.AppBlockerService
import com.zenlauncher.data.services.UsageTrackingService
import com.zenlauncher.databinding.ActivityMainBinding
import com.zenlauncher.presentation.home.HomeFragment
import com.zenlauncher.presentation.apps.AppsFragment
import com.zenlauncher.presentation.focus.FocusFragment
import com.zenlauncher.presentation.ranking.RankingFragment
import com.zenlauncher.presentation.stats.StatsFragment
import com.zenlauncher.presentation.permissions.UsagePermissionActivity
import com.zenlauncher.presentation.standby.StandbyActivity
import com.zenlauncher.MindfulLauncherApp
import com.zenlauncher.domain.interfaces.ChargingStateListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal do MindfulLauncher.
 * 
 * Esta atividade serve como o ponto de entrada do launcher e contém
 * a navegação por bottom navigation entre os diferentes fragmentos.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ChargingStateListener {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var powerReceiver: PowerConnectionReceiver
    private var currentFragment: Fragment? = null
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val FRAGMENT_HOME = "fragment_home"
        private const val FRAGMENT_APPS = "fragment_apps"
        private const val FRAGMENT_FOCUS = "fragment_focus"
        private const val FRAGMENT_RANKING = "fragment_ranking"
        private const val FRAGMENT_STATS = "fragment_stats"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        startServices()
        registerPowerReceiver()
        
        // Carregar fragment inicial se não há estado salvo
        if (savedInstanceState == null) {
            showFragment(HomeFragment(), FRAGMENT_HOME)
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
        
        // Verificar permissões de uso
        checkUsagePermission()
        
        // Verificar se o dispositivo está carregando ao iniciar
        if (PowerConnectionReceiver.isCharging(this)) {
            navigateToStandby()
        }
    }
    
    /**
     * Configura o Bottom Navigation e seus listeners.
     */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(HomeFragment(), FRAGMENT_HOME)
                    true
                }
                R.id.nav_apps -> {
                    showFragment(AppsFragment(), FRAGMENT_APPS)
                    true
                }
                R.id.nav_focus -> {
                    showFragment(FocusFragment(), FRAGMENT_FOCUS)
                    true
                }
                R.id.nav_ranking -> {
                    showFragment(RankingFragment(), FRAGMENT_RANKING)
                    true
                }
                R.id.nav_stats -> {
                    showFragment(StatsFragment(), FRAGMENT_STATS)
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * Mostra um fragment no container principal.
     */
    private fun showFragment(fragment: Fragment, tag: String) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        
        // Esconder fragment atual se existir
        currentFragment?.let {
            transaction.hide(it)
        }
        
        // Verificar se o fragment já existe
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        if (existingFragment != null) {
            // Fragment já existe, apenas mostrar
            transaction.show(existingFragment)
            currentFragment = existingFragment
        } else {
            // Criar novo fragment
            transaction.add(R.id.fragmentContainer, fragment, tag)
            currentFragment = fragment
        }
        
        transaction.commit()
    }
    
    /**
     * Inicia os serviços necessários.
     */
    private fun startServices() {
        // Iniciar serviço de monitoramento de uso
        val usageServiceIntent = UsageTrackingService.getStartIntent(this)
        startService(usageServiceIntent)
        
        // Iniciar serviço de bloqueio de aplicativos
        val blockerServiceIntent = AppBlockerService.getStartIntent(this)
        startService(blockerServiceIntent)
    }
    
    /**
     * Registra o receiver para monitorar o estado de carregamento.
     */
    private fun registerPowerReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        
        // Registrar receiver para mudanças de energia
        powerReceiver = PowerConnectionReceiver()
        registerReceiver(powerReceiver, filter)
    }
    
    /**
     * Navega para a tela de standby.
     */
    private fun navigateToStandby() {
        val intent = Intent(this, StandbyActivity::class.java)
        startActivity(intent)
    }
    
    /**
     * Verifica se temos permissão para acessar estatísticas de uso.
     */
    private fun checkUsagePermission() {
        if (!hasUsageStatsPermission()) {
            // Mostrar tela de permissão após um pequeno delay para não interferir com a inicialização
            binding.fragmentContainer.postDelayed({
                showUsagePermissionScreen()
            }, 2000)
        }
    }
    
    /**
     * Verifica se temos permissão para acessar estatísticas de uso.
     */
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    /**
     * Mostra a tela de solicitação de permissão.
     */
    private fun showUsagePermissionScreen() {
        val intent = UsagePermissionActivity.createIntent(this)
        startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val permissionGranted = data?.getBooleanExtra("permission_granted", false) ?: false
            if (permissionGranted) {
                // Permissão concedida, iniciar monitoramento
                (application as MindfulLauncherApp).restartMonitoring()
            }
        }
    }
    
    override fun onBackPressed() {
        // Se não estiver na tela inicial, navega para a tela inicial
        // Se estiver na tela inicial, não faz nada (não fecha o launcher)
        if (binding.bottomNavigation.selectedItemId != R.id.nav_home) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Desregistrar receiver
        try {
            unregisterReceiver(powerReceiver)
        } catch (e: Exception) {
            // Ignorar se não estiver registrado
        }
    }
    
    // Implementação da interface ChargingStateListener
    override fun onChargingStarted() {
        // Dispositivo começou a carregar - navegar para modo standby
        navigateToStandby()
    }
    
    override fun onChargingStopped() {
        // Dispositivo parou de carregar - voltar para o launcher normal
        // Não fazemos nada aqui pois o StandbyActivity se fecha sozinho
    }
    
    override fun onBatteryLevelChanged(level: Int) {
        // Monitorar nível da bateria se necessário
        // Por enquanto não fazemos nada
    }
}
