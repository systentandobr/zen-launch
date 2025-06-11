package com.zenlauncher.presentation.common.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppInfo
import com.zenlauncher.domain.entities.AppInfoParcelable

/**
 * Dialog minimalista para menu de contexto de apps
 * Segue os princípios de design minimalista do MindfulLauncher
 */
class AppContextMenuDialog : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_APP_INFO = "app_info"
        
        fun newInstance(appInfo: AppInfo): AppContextMenuDialog {
            return AppContextMenuDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_APP_INFO, AppInfoParcelable.fromAppInfo(appInfo))
                }
            }
        }
    }
    
    interface OnMenuActionListener {
        fun onToggleTimeReminder(enabled: Boolean)
        fun onToggleFavorite()
        fun onBlockApp()
        fun onRenameApp()
        fun onHideApp()
        fun onMoveToFolder()
        fun onUninstallApp()
        fun onShowAppInfo()
    }
    
    private var listener: OnMenuActionListener? = null
    private lateinit var appInfo: AppInfoParcelable
    
    fun setOnMenuActionListener(listener: OnMenuActionListener) {
        this.listener = listener
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = arguments?.getParcelable(ARG_APP_INFO) 
            ?: throw IllegalArgumentException("AppInfo is required")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app_context_menu, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader(view)
        setupMenuItems(view)
    }
    
    private fun setupHeader(view: View) {
        view.findViewById<TextView>(R.id.appNameText).text = appInfo.displayLabel
    }
    
    private fun setupMenuItems(view: View) {
        // Lembrete de tempo ativo
        val timeReminderSwitch = view.findViewById<SwitchCompat>(R.id.timeReminderSwitch)
        timeReminderSwitch.isChecked = appInfo.hasTimeReminder
        timeReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            listener?.onToggleTimeReminder(isChecked)
        }
        
        // Adicionar aos favoritos
        val favoriteItem = view.findViewById<View>(R.id.favoriteItem)
        val favoriteText = view.findViewById<TextView>(R.id.favoriteText)
        favoriteText.text = if (appInfo.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
        favoriteItem.setOnClickListener {
            listener?.onToggleFavorite()
            dismiss()
        }
        
        // Bloquear
        view.findViewById<View>(R.id.blockItem).setOnClickListener {
            listener?.onBlockApp()
            dismiss()
        }
        
        // Renomear
        view.findViewById<View>(R.id.renameItem).setOnClickListener {
            listener?.onRenameApp()
            dismiss()
        }
        
        // Ocultar
        view.findViewById<View>(R.id.hideItem).setOnClickListener {
            listener?.onHideApp()
            dismiss()
        }
        
        // Mover para pasta
        view.findViewById<View>(R.id.moveToFolderItem).setOnClickListener {
            listener?.onMoveToFolder()
            dismiss()
        }
        
        // Desinstalar
        view.findViewById<View>(R.id.uninstallItem).setOnClickListener {
            listener?.onUninstallApp()
            dismiss()
        }
        
        // Informações do app
        view.findViewById<View>(R.id.appInfoItem).setOnClickListener {
            listener?.onShowAppInfo()
            dismiss()
        }
    }
}
