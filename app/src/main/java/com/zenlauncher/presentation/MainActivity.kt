package com.zenlauncher.presentation

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zenlauncher.R
import com.zenlauncher.data.receivers.PowerConnectionReceiver
import com.zenlauncher.data.services.UsageTrackingService
import com.zenlauncher.databinding.ActivityMainBinding
import com.zenlauncher.presentation.navigation.NavigationChargingListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Atividade principal do ZenLauncher.
 * 
 * Esta atividade serve como o ponto de entrada do launcher e contém
 * o NavHostFragment para navegação entre os diferentes fragmentos.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var powerReceiver: PowerConnectionReceiver
    
    @Inject
    lateinit var navigationChargingListener: NavigationChargingListener
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        startServices()
        registerPowerReceiver()
        
        // Configurar o listener de navegação
        navigationChargingListener.setNavController(navController)
        lifecycle.addObserver(navigationChargingListener)
        
        // Verificar se o dispositivo está carregando ao iniciar
        if (PowerConnectionReceiver.isCharging(this)) {
            navController.navigate(R.id.standbyFragment)
        }
    }
    
    /**
     * Configura a navegação.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Configurar navegação com bottom navigation se existir
        binding.bottomNavigation?.setupWithNavController(navController)
    }
    
    /**
     * Inicia os serviços necessários.
     */
    private fun startServices() {
        // Iniciar serviço de monitoramento de uso
        val serviceIntent = UsageTrackingService.getStartIntent(this)
        startService(serviceIntent)
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
    
    override fun onBackPressed() {
        // Se não estiver na tela inicial, navega para a tela inicial
        // Se estiver na tela inicial, não faz nada (não fecha o launcher)
        if (navController.currentDestination?.id != R.id.homeFragment) {
            navController.navigate(R.id.homeFragment)
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
}
