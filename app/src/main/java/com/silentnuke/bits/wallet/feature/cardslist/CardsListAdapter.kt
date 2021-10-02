package com.silentnuke.bits.wallet.feature.cardslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.databinding.CardItemBinding

class CardsListAdapter(private val listViewModel: CardsListViewModel) :
    ListAdapter<Card, CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(listViewModel, item)
    }
}

class CardViewHolder private constructor(val binding: CardItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(listViewModel: CardsListViewModel, item: Card) {
        binding.viewmodel = listViewModel
        binding.card = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CardItemBinding.inflate(layoutInflater, parent, false)

            return CardViewHolder(binding)
        }
    }
}

class CardDiffCallback : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem == newItem
    }
}
