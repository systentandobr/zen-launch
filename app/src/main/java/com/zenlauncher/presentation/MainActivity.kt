package com.zenlauncher.presentation

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.zenlauncher.R
import com.zenlauncher.data.receivers.PowerConnectionReceiver
import com.zenlauncher.data.services.AppBlockerService
import com.zenlauncher.data.services.UsageTrackingService
import com.zenlauncher.databinding.ActivityMainBinding
import com.zenlauncher.presentation.navigation.pager.MainPagerAdapter
import com.zenlauncher.presentation.navigation.pager.ViewPagerExtensions.setTouchSlop
import com.zenlauncher.presentation.permissions.UsagePermissionActivity
import com.zenlauncher.presentation.standby.StandbyActivity
import com.zenlauncher.ZenLauncherApp
import com.zenlauncher.domain.interfaces.ChargingStateListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal do ZenLauncher.
 * 
 * Esta atividade serve como o ponto de entrada do launcher e contém
 * o ViewPager2 para navegação entre os diferentes fragmentos.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ChargingStateListener {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var powerReceiver: PowerConnectionReceiver
    private val dots = ArrayList<ImageView>()
    
    companion object {
        private const val HOME_PAGE_INDEX = 0
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        startServices()
        registerPowerReceiver()
        
        // Verificar permissões de uso
        checkUsagePermission()
        
        // Verificar se o dispositivo está carregando ao iniciar
        if (PowerConnectionReceiver.isCharging(this)) {
            navigateToStandby()
        }
    }
    
    /**
     * Configura o ViewPager2 e seu indicador.
     */
    private fun setupViewPager() {
        pagerAdapter = MainPagerAdapter(this)
        
        binding.viewPager.apply {
            adapter = pagerAdapter
            // Definir sensibilidade de deslize
            val sensitivity = 0.75f // Ajustar conforme necessário
            val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            binding.viewPager.setTouchSlop((touchSlop * sensitivity).toInt())
            
            // Registrar callback de página
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Atualizar indicadores
                    updatePageIndicators(position)
                }
            })
        }
        
        // Configurar indicadores de página
        setupPageIndicators()
        updatePageIndicators(0) // Página inicial
    }
    
    /**
     * Configura os indicadores de página.
     */
    private fun setupPageIndicators() {
        dots.clear()
        binding.pageIndicator.removeAllViews()
        
        val size = pagerAdapter.itemCount
        
        // Criar pontos
        for (i in 0 until size) {
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            
            binding.pageIndicator.addView(dot)
            dots.add(dot)
        }
    }
    
    /**
     * Atualiza os indicadores de página.
     */
    private fun updatePageIndicators(currentPage: Int) {
        for (i in dots.indices) {
            if (i == currentPage) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot))
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_unselected))
            }
        }
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
     * Navega para uma página específica do ViewPager.
     */
    fun navigateToPage(page: MainPagerAdapter.Page) {
        binding.viewPager.setCurrentItem(pagerAdapter.getPositionForPage(page), true)
    }
    
    /**
     * Verifica se temos permissão para acessar estatísticas de uso.
     */
    private fun checkUsagePermission() {
        if (!hasUsageStatsPermission()) {
            // Mostrar tela de permissão após um pequeno delay para não interferir com a inicialização
            binding.viewPager.postDelayed({
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
                (application as ZenLauncherApp).restartMonitoring()
            }
        }
    }
    
    override fun onBackPressed() {
        // Se não estiver na tela inicial, navega para a tela inicial
        // Se estiver na tela inicial, não faz nada (não fecha o launcher)
        if (binding.viewPager.currentItem != HOME_PAGE_INDEX) {
            binding.viewPager.setCurrentItem(HOME_PAGE_INDEX, true)
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
