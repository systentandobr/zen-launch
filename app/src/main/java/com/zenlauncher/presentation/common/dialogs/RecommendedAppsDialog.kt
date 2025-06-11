package com.zenlauncher.presentation.common.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppInfo
import com.zenlauncher.domain.entities.AppInfoParcelable
import com.zenlauncher.presentation.common.adapters.RecommendedAppsAdapter

/**
 * Dialog para selecionar apps recomendados e configurar visibilidade na tela inicial
 */
class RecommendedAppsDialog : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_APP_LIST = "app_list"
        
        fun newInstance(apps: List<AppInfo>): RecommendedAppsDialog {
            return RecommendedAppsDialog().apply {
                arguments = Bundle().apply {
                    // Converter AppInfo para AppInfoParcelable
                    val parcelableApps = apps.map { AppInfoParcelable.fromAppInfo(it) }
                    putParcelableArrayList(ARG_APP_LIST, ArrayList(parcelableApps))
                }
            }
        }
    }
    
    interface OnAppsConfiguredListener {
        fun onAppsConfigured(configuredApps: Map<String, Boolean>) // packageName -> isVisibleOnHomeScreen
        fun onUseDefaultApp()
    }
    
    private var listener: OnAppsConfiguredListener? = null
    private lateinit var apps: List<AppInfoParcelable>
    private lateinit var adapter: RecommendedAppsAdapter
    private val appVisibilityMap = mutableMapOf<String, Boolean>()
    
    fun setOnAppsConfiguredListener(listener: OnAppsConfiguredListener) {
        this.listener = listener
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        apps = arguments?.getParcelableArrayList<AppInfoParcelable>(ARG_APP_LIST)
            ?: throw IllegalArgumentException("App list is required")
        
        // Inicializar mapa de visibilidade com valores atuais
        apps.forEach { app ->
            appVisibilityMap[app.packageName] = app.isVisibleOnHomeScreen
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_recommended_apps, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        setupButtons(view)
        setupHomeScreenToggle(view)
    }
    
    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.appsRecyclerView)
        
        adapter = RecommendedAppsAdapter(
            apps = apps,
            onAppSelected = { appParcelable ->
                // Aqui você pode adicionar lógica de seleção se necessário
            },
            onVisibilityChanged = { packageName, isVisible ->
                appVisibilityMap[packageName] = isVisible
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
    
    private fun setupButtons(view: View) {
        val useDefaultButton = view.findViewById<Button>(R.id.useDefaultButton)
        useDefaultButton.setOnClickListener {
            listener?.onUseDefaultApp()
            dismiss()
        }
        
        val confirmButton = view.findViewById<Button>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            listener?.onAppsConfigured(appVisibilityMap)
            dismiss()
        }
    }
    
    private fun setupHomeScreenToggle(view: View) {
        val homeScreenToggle = view.findViewById<SwitchCompat>(R.id.homeScreenVisibilityToggle)
        homeScreenToggle.setOnCheckedChangeListener { _, isChecked ->
            // Atualizar todos os apps de uma vez
            apps.forEach { app ->
                appVisibilityMap[app.packageName] = isChecked
            }
            adapter.updateAllVisibility(isChecked)
        }
    }
}
