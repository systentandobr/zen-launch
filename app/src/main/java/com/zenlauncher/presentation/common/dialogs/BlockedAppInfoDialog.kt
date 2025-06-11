package com.zenlauncher.presentation.common.dialogs

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zenlauncher.R
import com.zenlauncher.databinding.DialogBlockedAppInfoBinding
import com.zenlauncher.domain.entities.AppInfoParcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Dialog para mostrar informações sobre um app bloqueado.
 * Exibe quanto tempo restante de bloqueio e o motivo.
 */
@AndroidEntryPoint
class BlockedAppInfoDialog : DialogFragment() {
    
    private var _binding: DialogBlockedAppInfoBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BlockedAppInfoViewModel by viewModels()
    
    companion object {
        private const val ARG_APP_INFO = "app_info"
        
        fun newInstance(appInfo: AppInfoParcelable): BlockedAppInfoDialog {
            return BlockedAppInfoDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_APP_INFO, appInfo)
                }
            }
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBlockedAppInfoBinding.inflate(layoutInflater)
        
        val appInfo = arguments?.getParcelable<AppInfoParcelable>(ARG_APP_INFO)
            ?: throw IllegalArgumentException("AppInfo é obrigatório")
        
        setupUI(appInfo)
        setupObservers(appInfo)
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()
    }
    
    private fun setupUI(appInfo: AppInfoParcelable) {
        // Configurar informações básicas do app
        binding.appName.text = appInfo.displayLabel
        
        // Carregar ícone do app
        try {
            val appIcon = requireContext().packageManager.getApplicationIcon(appInfo.packageName)
            binding.appIcon.setImageDrawable(appIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.appIcon.setImageResource(R.drawable.ic_launcher_foreground)
        }
        
        // Configurar botão de fechar
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }
    
    private fun setupObservers(appInfo: AppInfoParcelable) {
        lifecycleScope.launch {
            viewModel.loadBlockInfo(appInfo.packageName)
            
            viewModel.blockInfo.collect { blockInfo ->
                if (blockInfo != null) {
                    updateBlockInfo(blockInfo)
                } else {
                    // App não está mais bloqueado
                    binding.statusText.text = "Este aplicativo não está mais bloqueado"
                    binding.remainingTimeText.text = ""
                    binding.reasonText.text = ""
                }
            }
        }
    }
    
    private fun updateBlockInfo(blockInfo: BlockedAppInfoViewModel.BlockInfo) {
        // Status do bloqueio
        binding.statusText.text = when (blockInfo.level) {
            "SOFT" -> "Bloqueio Suave - Apenas notificação"
            "MEDIUM" -> "Bloqueio Médio - Confirmação necessária"
            "HARD" -> "Bloqueio Rigoroso - Acesso negado"
            else -> "Aplicativo Bloqueado"
        }
        
        // Tempo restante
        if (blockInfo.remainingTimeMs > 0) {
            val remainingText = formatRemainingTime(blockInfo.remainingTimeMs)
            binding.remainingTimeText.text = "Tempo restante: $remainingText"
            
            // Também mostrar quando termina
            val endTime = Date(System.currentTimeMillis() + blockInfo.remainingTimeMs)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.endTimeText.text = "Termina às ${timeFormat.format(endTime)}"
        } else {
            binding.remainingTimeText.text = "Bloqueio expirado"
            binding.endTimeText.text = ""
        }
        
        // Motivo do bloqueio
        binding.reasonText.text = blockInfo.reason ?: "Bloqueio manual"
    }
    
    private fun formatRemainingTime(remainingTimeMs: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMs) % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}min"
            minutes > 0 -> "${minutes} minutos"
            else -> "Menos de 1 minuto"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
