package com.zenlauncher.presentation.stats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppUsageBinding
import com.zenlauncher.domain.entities.AppUsageStats

/**
 * Adapter para exibir estatísticas de uso de aplicativos.
 */
class AppUsageAdapter : ListAdapter<AppUsageStats, AppUsageAdapter.ViewHolder>(AppUsageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(
        private val binding: ItemAppUsageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(usageStats: AppUsageStats) {
            binding.appName.text = usageStats.appName
            
            // Formatar tempo de uso
            val hours = usageStats.usageTimeWeek / (1000 * 60 * 60)
            val minutes = (usageStats.usageTimeWeek % (1000 * 60 * 60)) / (1000 * 60)
            
            val usageText = if (hours > 0) {
                "$hours h $minutes min"
            } else {
                "$minutes min"
            }
            binding.usageTime.text = usageText
            
            // Configurar a barra de progresso
            val maxUsageTime = 4 * 60 * 60 * 1000 // 4 horas como máximo
            val progress = (usageStats.usageTimeWeek * 100 / maxUsageTime).toInt().coerceAtMost(100)
            binding.progressBar.progress = progress
            
            // Carregar ícone do aplicativo
            try {
                val packageManager = binding.root.context.packageManager
                binding.appIcon.setImageDrawable(
                    packageManager.getApplicationIcon(usageStats.packageName)
                )
            } catch (e: Exception) {
                // Fallback para ícone padrão
                binding.appIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }
    }
    
    private class AppUsageDiffCallback : DiffUtil.ItemCallback<AppUsageStats>() {
        override fun areItemsTheSame(oldItem: AppUsageStats, newItem: AppUsageStats): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppUsageStats, newItem: AppUsageStats): Boolean {
            return oldItem == newItem
        }
    }
}
