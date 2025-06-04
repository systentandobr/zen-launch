package com.zenlauncher.presentation

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
import com.zenlauncher.presentation.standby.StandbyActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal do ZenLauncher.
 * 
 * Esta atividade serve como o ponto de entrada do launcher e contém
 * o ViewPager2 para navegação entre os diferentes fragmentos.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var powerReceiver: PowerConnectionReceiver
    private val dots = ArrayList<ImageView>()
    
    companion object {
        private const val HOME_PAGE_INDEX = 0
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        startServices()
        registerPowerReceiver()
        
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
}
