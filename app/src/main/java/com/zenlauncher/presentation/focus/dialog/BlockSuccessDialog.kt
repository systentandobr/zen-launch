package com.zenlauncher.presentation.focus.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zenlauncher.databinding.DialogBlockSuccessBinding

/**
 * Diálogo para mostrar o sucesso de bloqueio de aplicativos.
 */
class BlockSuccessDialog : DialogFragment() {
    
    private var _binding: DialogBlockSuccessBinding? = null
    private val binding get() = _binding!!
    
    private var onDismissListener: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBlockSuccessBinding.inflate(layoutInflater)
        
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
            dismiss()
            onDismissListener?.invoke()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Define o listener para quando o diálogo é fechado.
     */
    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }
}
