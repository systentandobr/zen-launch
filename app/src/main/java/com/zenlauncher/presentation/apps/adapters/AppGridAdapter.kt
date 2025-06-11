package com.zenlauncher.presentation.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppGridBinding
import com.zenlauncher.domain.entities.AppInfo

/**
 * Adaptador para exibir aplicativos em formato de grade horizontal dentro das categorias.
 * 
 * @property onAppClick Callback acionado quando um aplicativo é clicado
 * @property onAppLongClick Callback acionado quando um aplicativo é pressionado por tempo prolongado
 */
class AppGridAdapter(
    private val onAppClick: (AppInfo) -> Unit,
    private val onAppLongClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppGridAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppGridBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemAppGridBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: AppInfo) {
            binding.appName.text = app.label
            binding.appIcon.setImageDrawable(app.icon)
            
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
