package com.cheatart.newcheatcodes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.StoreItemBinding
import com.cheatart.newcheatcodes.model.GameData
import com.cheatart.newcheatcodes.model.Store
import com.cheatart.newcheatcodes.model.Stores

class StoresAdapter(
    private val onStoreClickListener: OnStoresItemClickListener
) :
    ListAdapter<Stores, StoresAdapter.StoresViewHolder>(STORES_COMPARATOR),
    AdapterView.OnItemClickListener {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoresViewHolder {
        val binding =
            StoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

    }


    interface OnStoresItemClickListener {
        fun onStoreItemClick(store: Stores?)
    }

    inner class StoresViewHolder(private val binding: StoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onStoreClickListener.onStoreItemClick(item)
                }
            }
        }


        fun bind(store: Stores) {
            binding.apply {
                storeImage.setImageResource(store.store?.image!!)
                storeName.text = store.store.name
            }
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    companion object {
        private val STORES_COMPARATOR = object : DiffUtil.ItemCallback<Stores>() {
            override fun areItemsTheSame(
                oldItem: Stores,
                newItem: Stores
            ) =
                oldItem.store?.id == newItem.store?.id

            override fun areContentsTheSame(
                oldItem: Stores,
                newItem: Stores
            ) =
                oldItem == newItem

        }
    }
}