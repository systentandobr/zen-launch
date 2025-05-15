package com.zenlauncher.presentation.focus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppBlockBinding
import com.zenlauncher.domain.entities.App

/**
 * Adapter para exibir aplicativos com checkbox de seleção.
 */
class AppBlockAdapter(
    private val onAppClick: (App) -> Unit,
    private val selectedPackages: Set<String>
) : ListAdapter<App, AppBlockAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBlockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAppBlockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppClick(getItem(position))
                }
            }
        }

        fun bind(app: App) {
            binding.appName.text = app.appName
            binding.packageName.text = app.packageName
            binding.checkBox.isChecked = selectedPackages.contains(app.packageName)
            
            // Carregar ícone do aplicativo
            try {
                val packageManager = binding.root.context.packageManager
                binding.appIcon.setImageDrawable(
                    packageManager.getApplicationIcon(app.packageName)
                )
            } catch (e: Exception) {
                // Fallback para ícone padrão
                binding.appIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }
    }

    /**
     * Atualiza o conjunto de pacotes selecionados.
     */
    fun updateSelectedPackages(selectedPackages: Set<String>) {
        // Apenas notificar itens que mudaram de estado
        val oldSelection = this.selectedPackages
        
        for (i in 0 until itemCount) {
            val app = getItem(i)
            val wasSelected = oldSelection.contains(app.packageName)
            val isSelected = selectedPackages.contains(app.packageName)
            
            if (wasSelected != isSelected) {
                notifyItemChanged(i)
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
