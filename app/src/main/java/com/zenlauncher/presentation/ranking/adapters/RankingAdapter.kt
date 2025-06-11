package com.zenlauncher.presentation.ranking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenlauncher.databinding.ItemRankingBinding
import com.zenlauncher.presentation.ranking.model.RankingEntry

/**
 * Adapter para exibir lista de ranking de usuários.
 */
class RankingAdapter(
    private val onItemClick: (RankingEntry) -> Unit
) : ListAdapter<RankingEntry, RankingAdapter.RankingViewHolder>(RankingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RankingViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RankingViewHolder(
        private val binding: ItemRankingBinding,
        private val onItemClick: (RankingEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rankingEntry: RankingEntry) {
            binding.apply {
                tvPosition.text = rankingEntry.position.toString()
                tvUsername.text = rankingEntry.username
                tvReduction.text = rankingEntry.reduction
                tvPoints.text = rankingEntry.points.toString()
                tvStreak.text = "🔥 ${rankingEntry.streakDays} dias"
                
                // Destacar se é o usuário atual
                if (rankingEntry.isCurrentUser) {
                    tvUsername.setTextColor(
                        root.context.getColor(android.R.color.holo_green_light)
                    )
                }
                
                root.setOnClickListener {
                    onItemClick(rankingEntry)
                }
            }
        }
    }

    private class RankingDiffCallback : DiffUtil.ItemCallback<RankingEntry>() {
        override fun areItemsTheSame(oldItem: RankingEntry, newItem: RankingEntry): Boolean {
            return oldItem.position == newItem.position && oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: RankingEntry, newItem: RankingEntry): Boolean {
            return oldItem == newItem
        }
    }
}
