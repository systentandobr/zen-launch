package com.zenlauncher.presentation.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppInfoParcelable
import com.zenlauncher.presentation.common.extensions.loadIcon

/**
 * Adapter para exibir lista de apps recomendados com toggle de visibilidade
 */
class RecommendedAppsAdapter(
    private val apps: List<AppInfoParcelable>,
    private val onAppSelected: (AppInfoParcelable) -> Unit,
    private val onVisibilityChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecommendedAppsAdapter.ViewHolder>() {

    private val visibilityMap = mutableMapOf<String, Boolean>()
    
    init {
        // Inicializar mapa de visibilidade
        apps.forEach { app ->
            visibilityMap[app.packageName] = app.isVisibleOnHomeScreen
        }
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        val appName: TextView = itemView.findViewById(R.id.appName)
        val checkmark: ImageView = itemView.findViewById(R.id.checkmark)
        
        fun bind(app: AppInfoParcelable) {
            appIcon.setImageDrawable(app.loadIcon(itemView.context))
            appName.text = app.displayLabel
            
            // Mostrar checkmark se o app estiver selecionado
            val isSelected = visibilityMap[app.packageName] ?: true
            checkmark.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            // Atualizar visual baseado na seleção
            itemView.alpha = if (isSelected) 1.0f else 0.5f
            
            itemView.setOnClickListener {
                val newState = !(visibilityMap[app.packageName] ?: true)
                visibilityMap[app.packageName] = newState
                onVisibilityChanged(app.packageName, newState)
                
                // Usar adapterPosition com verificação de validade
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position)
                }
                
                onAppSelected(app)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended_app, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(apps[position])
    }
    
    override fun getItemCount() = apps.size
    
    fun updateAllVisibility(isVisible: Boolean) {
        apps.forEach { app ->
            visibilityMap[app.packageName] = isVisible
        }
        notifyDataSetChanged()
    }
}
