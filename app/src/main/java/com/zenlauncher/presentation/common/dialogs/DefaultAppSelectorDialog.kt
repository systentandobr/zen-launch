package com.zenlauncher.presentation.common.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppInfo
import com.zenlauncher.domain.entities.AppInfoParcelable
import com.zenlauncher.presentation.common.views.SlideToConfirmView

/**
 * Dialog para seleção de app padrão com confirmação deslizante
 * Usado para configurar apps padrão para ações específicas (Câmera, Telefone, etc.)
 */
class DefaultAppSelectorDialog : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_ACTION_TYPE = "action_type"
        private const val ARG_SELECTED_APP = "selected_app"
        
        enum class ActionType {
            CAMERA,
            PHONE,
            ALARM_CLOCK
        }
        
        fun newInstance(actionType: ActionType, selectedApp: AppInfo): DefaultAppSelectorDialog {
            return DefaultAppSelectorDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ACTION_TYPE, actionType)
                    putParcelable(ARG_SELECTED_APP, AppInfoParcelable.fromAppInfo(selectedApp))
                }
            }
        }
    }
    
    interface OnAppSelectedListener {
        fun onAppSelected(packageName: String)
        fun onUseDefaultApp()
    }
    
    private var listener: OnAppSelectedListener? = null
    private lateinit var actionType: ActionType
    private lateinit var selectedApp: AppInfoParcelable
    
    fun setOnAppSelectedListener(listener: OnAppSelectedListener) {
        this.listener = listener
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        arguments?.let {
            actionType = it.getSerializable(ARG_ACTION_TYPE) as ActionType
            selectedApp = it.getParcelable(ARG_SELECTED_APP)
                ?: throw IllegalArgumentException("Selected app is required")
        } ?: throw IllegalArgumentException("Arguments are required")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_default_app_selector, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader(view)
        setupSlideToConfirm(view)
        setupButtons(view)
    }
    
    private fun setupHeader(view: View) {
        val titleText = view.findViewById<TextView>(R.id.titleText)
        titleText.text = when (actionType) {
            ActionType.CAMERA -> "Escolher app de Câmera"
            ActionType.PHONE -> "Escolher app de Telefone"
            ActionType.ALARM_CLOCK -> "Escolher app de Despertador"
        }
    }
    
    private fun setupSlideToConfirm(view: View) {
        val slideToConfirm = view.findViewById<SlideToConfirmView>(R.id.slideToConfirm)
        slideToConfirm.setOnSlideCompleteListener(object : SlideToConfirmView.OnSlideCompleteListener {
            override fun onSlideComplete() {
                listener?.onAppSelected(selectedApp.packageName)
                dismiss()
            }
        })
    }
    
    private fun setupButtons(view: View) {
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        val useDefaultButton = view.findViewById<Button>(R.id.useDefaultButton)
        useDefaultButton.setOnClickListener {
            listener?.onUseDefaultApp()
            dismiss()
        }
    }
}
