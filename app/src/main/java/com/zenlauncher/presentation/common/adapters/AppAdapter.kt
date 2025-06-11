package com.zenlauncher.presentation.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppBinding
import com.zenlauncher.domain.entities.AppInfo

/**
 * Adaptador para exibir aplicativos em lista ou grade.
 * 
 * @property onAppClick Callback acionado quando um aplicativo é clicado
 * @property onAppLongClick Callback acionado quando um aplicativo é pressionado por tempo prolongado
 */
class AppAdapter(
    private val onAppClick: (AppInfo) -> Unit,
    private val onAppLongClick: (AppInfo) -> Unit,
    private val onBlockedAppLongClick: ((AppInfo) -> Unit)? = null
) : ListAdapter<AppInfo, AppAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)
        holder.bind(app)
    }

    inner class ViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: AppInfo) {
            binding.appName.text = app.displayLabel
            binding.appIcon.setImageDrawable(app.icon)
            
            // Configurar estado de favorito
            if (app.isFavorite) {
                binding.favoriteIcon.visibility = android.view.View.VISIBLE
            } else {
                binding.favoriteIcon.visibility = android.view.View.GONE
            }
            
            // Configurar estado de bloqueio
            if (app.isBlocked) {
                binding.blockedIcon.visibility = android.view.View.VISIBLE
                // Deixar o item com aparência desabilitada
                binding.root.alpha = 0.6f
            } else {
                binding.blockedIcon.visibility = android.view.View.GONE
                binding.root.alpha = 1.0f
            }
            
            binding.root.setOnClickListener {
                if (!app.isBlocked) {
                    onAppClick(app)
                }
            }
            
            binding.root.setOnLongClickListener {
                if (app.isBlocked && onBlockedAppLongClick != null) {
                    onBlockedAppLongClick.invoke(app)
                } else {
                    onAppLongClick(app)
                }
                true
            }
        }
    }
    
    private class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}
