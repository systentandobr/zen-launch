package com.zenlauncher.presentation.focus.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zenlauncher.databinding.DialogBlockConfirmationBinding
import com.zenlauncher.domain.entities.AppBlock
import java.util.Date

/**
 * Diálogo para confirmar o bloqueio de aplicativos.
 */
class BlockConfirmationDialog : DialogFragment() {
    
    private var _binding: DialogBlockConfirmationBinding? = null
    private val binding get() = _binding!!
    
    private var onConfirmListener: ((AppBlock) -> Unit)? = null
    private var appCount: Int = 0;
    private val quatroHorasEmMillis: Long = 4 * 60 * 60 * 1000L;
    private var durationHours: Long = quatroHorasEmMillis;
    private var blockLevel: AppBlock.BlockLevel = AppBlock.BlockLevel.MEDIUM
    private var appBlock: AppBlock = AppBlock("", Date(), blockLevel);
    
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBlockConfirmationBinding.inflate(layoutInflater)
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        updateUI()
        
        binding.blockLevelRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            blockLevel = when (checkedId) {
                binding.radioSoft.id -> AppBlock.BlockLevel.SOFT
                binding.radioMedium.id -> AppBlock.BlockLevel.MEDIUM
                binding.radioHard.id -> AppBlock.BlockLevel.HARD
                else -> AppBlock.BlockLevel.MEDIUM
            }
        }
        
        binding.confirmButton.setOnClickListener {
            onConfirmListener?.invoke(appBlock)
            dismiss()
        }
        
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
    
    private fun updateUI() {
        // Configurar detalhes do bloqueio
        val appText = if (appCount == 1) "1 aplicativo" else "$appCount aplicativos"
        binding.titleTextView.text = "Confirmar Bloqueio"
        
        // Formatar duração
        val durationText = when {
            durationHours < 1 -> "${(durationHours * 60).toInt()} minutos"
            durationHours == quatroHorasEmMillis -> "4 horas"
            durationHours.toInt().toLong() == durationHours -> "${durationHours.toInt()} horas"
            else -> {
                val h = durationHours.toInt()
                val m = ((durationHours - h) * 60).toInt()
                "$h horas e $m minutos"
            }
        }
        
        binding.messageTextView.text = "Você está prestes a bloquear $appText por $durationText. " +
                "Escolha o nível de bloqueio desejado:"
        
        // Configurar rádio selecionado
        when (blockLevel) {
            AppBlock.BlockLevel.SOFT -> binding.radioSoft.isChecked = true
            AppBlock.BlockLevel.MEDIUM -> binding.radioMedium.isChecked = true
            AppBlock.BlockLevel.HARD -> binding.radioHard.isChecked = true
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Define o listener para o botão de confirmação.
     */
    fun setOnConfirmListener(listener: (AppBlock) -> Unit) {
        onConfirmListener = listener
    }
    
    /**
     * Configura os detalhes do bloqueio.
     */
    fun setBlockDetails(appCount: Int, durationHours: Long, blockLevel: AppBlock.BlockLevel) {
        this.appCount = appCount
        this.durationHours = durationHours
        this.blockLevel = blockLevel
        appBlock = AppBlock("", Date(durationHours), blockLevel);
        if (_binding != null) {
            updateUI()
        }
    }
    
    companion object {
        /**
         * Cria uma nova instância do diálogo.
         */
        fun newInstance(appCount: Int, durationHours: Long, blockLevel: AppBlock.BlockLevel): BlockConfirmationDialog {
            return BlockConfirmationDialog().apply {
                setBlockDetails(appCount, durationHours, blockLevel)
            }
        }
    }
}
