package com.zenlauncher.presentation.focus.blockscreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zenlauncher.R
import com.zenlauncher.databinding.DialogAppBlockUnlockBinding

/**
 * Diálogo para confirmar a remoção de um bloqueio de aplicativo.
 */
class AppBlockUnlockDialog : DialogFragment() {
    
    private var _binding: DialogAppBlockUnlockBinding? = null
    private val binding get() = _binding!!
    
    private var onConfirmListener: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAppBlockUnlockBinding.inflate(layoutInflater)
        
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
        
        binding.confirmButton.setOnClickListener {
            onConfirmListener?.invoke()
            dismiss()
        }
        
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Define o listener para o botão de confirmação.
     */
    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirmListener = listener
    }
}
