package com.zenlauncher.presentation.focus.blockscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zenlauncher.databinding.ActivityAppBlockScreenBinding
import com.zenlauncher.domain.entities.AppBlock
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Atividade exibida quando um aplicativo bloqueado é detectado.
 * 
 * Esta tela impede o usuário de acessar o aplicativo bloqueado
 * e exibe informações sobre o bloqueio, incluindo:
 * - Nome do aplicativo
 * - Tempo restante
 * - Nível de bloqueio
 * - Opções para estender ou cancelar o bloqueio
 */
@AndroidEntryPoint
class AppBlockScreenActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAppBlockScreenBinding
    private val viewModel: AppBlockScreenViewModel by viewModels()
    
    private var packageName: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBlockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        
        if (packageName == null) {
            finish()
            return
        }
        
        setupUI()
        setupObservers()
        
        viewModel.loadAppBlockInfo(packageName!!)
    }
    
    private fun setupUI() {
        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.extendBlockButton.setOnClickListener {
            viewModel.extendBlockTime(TimeUnit.MINUTES.toMillis(15))
        }
        
        binding.unblockButton.setOnClickListener {
            // Mostrar diálogo de confirmação antes de desbloquear
            AppBlockUnlockDialog().apply {
                setOnConfirmListener {
                    viewModel.unblockApp()
                }
            }.show(supportFragmentManager, "unblock_dialog")
        }
        
        binding.settingsButton.setOnClickListener {
            // Aqui pode navegar para as configurações de bloqueio de aplicativos
            finish()
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.appBlock.collectLatest { block ->
                if (block == null) {
                    // Se não houver bloqueio, retorna à tela anterior
                    finish()
                    return@collectLatest
                }
                
                updateBlockInfo(block)
            }
        }
        
        lifecycleScope.launch {
            viewModel.appInfo.collectLatest { appInfo ->
                binding.appNameTextView.text = appInfo?.label ?: packageName
                binding.appIcon.setImageDrawable(appInfo?.icon)
            }
        }
        
        lifecycleScope.launch {
            viewModel.remainingTime.collectLatest { remainingTime ->
                updateRemainingTime(remainingTime)
            }
        }
        
        lifecycleScope.launch {
            viewModel.isUnblocked.collectLatest { isUnblocked ->
                if (isUnblocked) {
                    // Se foi desbloqueado, retorna à tela anterior
                    finish()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.blockLevelDescription.collectLatest { description ->
                binding.blockLevelTextView.text = description
            }
        }
    }
    
    /**
     * Atualiza as informações de bloqueio na UI.
     */
    private fun updateBlockInfo(block: AppBlock) {
        // Configurar UI com base no nível de bloqueio
        when (block.blockLevel) {
            AppBlock.BlockLevel.SOFT -> {
                binding.blockTypeIcon.setImageResource(android.R.drawable.ic_menu_info_details)
                binding.blockLevelTextView.text = "Apenas Lembrete"
                binding.blockMessageTextView.text = "Este aplicativo está em sua lista de bloqueio. Você ainda pode utilizá-lo, mas recomendamos focar em outras atividades."
                
                // Permitir continuar em nível SOFT
                binding.continueButton.visibility = View.VISIBLE
                binding.continueButton.setOnClickListener {
                    finish()
                }
            }
            
            AppBlock.BlockLevel.MEDIUM -> {
                binding.blockTypeIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                binding.blockLevelTextView.text = "Bloqueio Médio"
                binding.blockMessageTextView.text = "Este aplicativo está bloqueado para ajudar em sua concentração. Você ainda pode acessá-lo se realmente necessário."
                
                // Permitir continuar após confirmação em nível MEDIUM
                binding.continueButton.visibility = View.VISIBLE
                binding.continueButton.setOnClickListener {
                    // Mostra diálogo de confirmação antes de permitir uso
                    AppBlockContinueDialog().apply {
                        setOnConfirmListener {
                            finish()
                        }
                    }.show(supportFragmentManager, "continue_dialog")
                }
            }
            
            AppBlock.BlockLevel.HARD -> {
                binding.blockTypeIcon.setImageResource(android.R.drawable.ic_lock_lock)
                binding.blockLevelTextView.text = "Bloqueio Rígido"
                binding.blockMessageTextView.text = "Este aplicativo está rigorosamente bloqueado para máxima concentração. Você só poderá acessá-lo após o término do período de bloqueio."
                
                // Não permitir continuar em nível HARD
                binding.continueButton.visibility = View.GONE
            }
        }
    }
    
    /**
     * Atualiza o tempo restante na UI.
     */
    private fun updateRemainingTime(remainingTimeMs: Long) {
        if (remainingTimeMs <= 0) {
            binding.remainingTimeTextView.text = "Bloqueio expirado"
            binding.remainingTimeProgressBar.progress = 0
            binding.remainingTimeProgressBar.max = 100
            finish() // Fechar a tela se o bloqueio já expirou
            return
        }
        
        // Formatar tempo restante
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMs) % 60
        
        val formattedTime = when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
        
        binding.remainingTimeTextView.text = "Tempo restante: $formattedTime"
        
        // Formatar data de término
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val endTime = System.currentTimeMillis() + remainingTimeMs
        binding.endTimeTextView.text = "Bloqueado até: ${dateFormat.format(endTime)}"
        
        // Atualizar barra de progresso (estimando tempo inicial como máximo 24 horas)
        val initialTimeMs = 24 * 60 * 60 * 1000L // 24 horas em milissegundos
        val progressPercent = ((initialTimeMs - remainingTimeMs) / initialTimeMs.toFloat() * 100).toInt()
        binding.remainingTimeProgressBar.progress = progressPercent.coerceIn(0, 100)
        binding.remainingTimeProgressBar.max = 100
    }
    
    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }
}
