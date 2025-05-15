package com.zenlauncher.presentation.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemAppFavoriteBinding
import com.zenlauncher.domain.entities.App

/**
 * Adapter para exibir aplicativos no RecyclerView.
 * 
 * @param onAppClick Callback para quando um aplicativo for clicado
 * @param onAppLongClick Callback para quando um aplicativo for pressionado por tempo longo
 */
class AppAdapter(
    private val onAppClick: (App) -> Unit,
    private val onAppLongClick: (App) -> Unit = {}
) : ListAdapter<App, AppAdapter.AppViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppViewHolder(
        private val binding: ItemAppFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppClick(getItem(position))
                }
            }
            
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppLongClick(getItem(position))
                }
                true
            }
        }

        fun bind(app: App) {
            binding.appName.text = app.appName
            
            // Carregar ícone do aplicativo usando o PackageManager
            // Será implementado posteriormente com uma classe utilitária
            try {
                val packageManager = binding.root.context.packageManager
                binding.appIcon.setImageDrawable(
                    packageManager.getApplicationIcon(app.packageName)
                )
            } catch (e: Exception) {
                // Fallback para um ícone padrão em caso de erro
                binding.appIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }
    }

    /**
     * DiffUtil.Callback para otimizar atualizações na lista de aplicativos.
     */
    private class AppDiffCallback : DiffUtil.ItemCallback<App>() {
        override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem == newItem
        }
    }
}
