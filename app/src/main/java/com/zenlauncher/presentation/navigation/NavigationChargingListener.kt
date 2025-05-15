package com.zenlauncher.presentation.navigation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.zenlauncher.R
import com.zenlauncher.domain.interfaces.ChargingStateListener
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de ChargingStateListener que cuida da navegação
 * baseada no estado de carregamento.
 */
@Singleton
class NavigationChargingListener @Inject constructor() : ChargingStateListener, DefaultLifecycleObserver {
    
    private var navController: NavController? = null
    
    /**
     * Define o NavController para navegação.
     * Este método deve ser chamado pela Activity no onCreate.
     */
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        // Limpar referência quando a atividade for destruída
        navController = null
    }
    
    override fun onChargingStarted() {
        navController?.let { nav ->
            // Verificar se não está já no modo standby
            if (nav.currentDestination?.id != R.id.standbyFragment) {
                nav.navigate(R.id.standbyFragment)
            }
        }
    }
    
    override fun onChargingStopped() {
        navController?.let { nav ->
            // Se estiver no modo standby, voltar à tela inicial
            if (nav.currentDestination?.id == R.id.standbyFragment) {
                nav.navigate(R.id.homeFragment)
            }
        }
    }
    
    override fun onBatteryLevelChanged(level: Int) {
        // Nada a fazer aqui
    }
}
