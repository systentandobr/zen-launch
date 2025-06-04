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
    private val onAppLongClick: (AppInfo) -> Unit
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
            binding.appName.text = app.label
            binding.appIcon.setImageDrawable(app.icon)
            
            // Configurar estado de favorito, se aplicável
            if (app.isFavorite) {
                binding.favoriteIcon.visibility = android.view.View.VISIBLE
            } else {
                binding.favoriteIcon.visibility = android.view.View.GONE
            }
            
            binding.root.setOnClickListener {
                onAppClick(app)
            }
            
            binding.root.setOnLongClickListener {
                onAppLongClick(app)
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
