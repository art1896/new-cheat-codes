package com.cheatart.newcheatcodes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.ListItemBinding
import com.cheatart.newcheatcodes.db.Game
import com.squareup.picasso.Picasso

class FavoritesAdapter(
    private val listener: OnItemClickListener
) :
    ListAdapter<Game, RecyclerView.ViewHolder>(GAME_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            (holder as FavoritesAdapter.FavoritesViewHolder).bind(currentItem)
        }

    }


    inner class FavoritesViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(game: Game) {
            binding.apply {
                Picasso.get().load(game.gameImage).fit().centerCrop().into(posterImage)
                gameName.text = game.gameName
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(game: Game)
    }


    companion object {
        private val GAME_COMPARATOR = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Game, newItem: Game) =
                oldItem == newItem


        }
    }

}