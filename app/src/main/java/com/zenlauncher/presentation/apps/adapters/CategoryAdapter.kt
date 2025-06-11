package com.zenlauncher.presentation.apps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemCategoryBinding
import com.zenlauncher.domain.entities.AppCategory
import com.zenlauncher.domain.entities.AppInfo

/**
 * Adaptador para exibir categorias de aplicativos.
 * 
 * @property onAppClick Callback acionado quando um aplicativo é clicado
 * @property onAppLongClick Callback acionado quando um aplicativo é pressionado por tempo prolongado
 */
class CategoryAdapter(
    private val onAppClick: (AppInfo) -> Unit,
    private val onAppLongClick: (AppInfo) -> Unit
) : ListAdapter<AppCategory, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val appsAdapter = AppGridAdapter(onAppClick, onAppLongClick)
        
        fun bind(category: AppCategory) {
            binding.categoryTitle.text = category.title
            
            // Configurar o RecyclerView horizontal
            binding.appsRecyclerView.apply {
                adapter = appsAdapter
                // Layout horizontal para os apps dentro da categoria
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                    context, 
                    androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
            
            // Atualizar a lista de apps
            appsAdapter.submitList(category.apps)
            
            // Exibir "Ver mais" apenas se houver mais de 5 apps
            binding.viewMoreButton.visibility = if (category.apps.size > 5) View.VISIBLE else View.GONE
            
            // Configurar ação do botão "Ver mais"
            binding.viewMoreButton.setOnClickListener {
                // Implementar ação para ver todos os apps da categoria
                // Será implementado posteriormente
            }
        }
    }
    
    private class CategoryDiffCallback : DiffUtil.ItemCallback<AppCategory>() {
        override fun areItemsTheSame(oldItem: AppCategory, newItem: AppCategory): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: AppCategory, newItem: AppCategory): Boolean {
            return oldItem == newItem
        }
    }
}
