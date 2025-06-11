package com.zenlauncher.presentation.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppFavoriteBinding
import com.zenlauncher.domain.entities.App

/**
 * Adaptador para exibir aplicativos favoritos na tela inicial.
 * 
 * @property onAppClick Callback acionado quando um aplicativo é clicado
 * @property onAppLongClick Callback acionado quando um aplicativo é pressionado por tempo prolongado
 */
class FavoriteAppsAdapter(
    private val onAppClick: (App) -> Unit,
    private val onAppLongClick: (App) -> Unit
) : ListAdapter<App, FavoriteAppsAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppFavoriteBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemAppFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: App) {
            binding.appName.text = app.appName
            // binding.appIcon.setImageDrawable(app.icon)
            
            binding.root.setOnClickListener {
                onAppClick(app)
            }
            
            binding.root.setOnLongClickListener {
                onAppLongClick(app)
                true
            }
        }
    }
    
    private class AppDiffCallback : DiffUtil.ItemCallback<App>() {
        override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem == newItem
        }
    }
}
