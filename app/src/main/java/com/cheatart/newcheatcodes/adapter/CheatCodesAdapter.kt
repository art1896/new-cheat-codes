package com.cheatart.newcheatcodes.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.CheatItemBinding
import com.cheatart.newcheatcodes.model.Code
import java.util.*

class CheatCodesAdapter :
    ListAdapter<Code, CheatCodesAdapter.CheatCodesViewHolder>(CHEAT_CODES_COMPARATOR), Filterable {

    private var list = mutableListOf<Code>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheatCodesAdapter.CheatCodesViewHolder {
        val binding = CheatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        return CheatCodesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheatCodesAdapter.CheatCodesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun setData(list: MutableList<Code>?) {
        this.list = list!!
        submitList(list)
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Code>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(list)
            } else {
                for (item in list) {
                    if (item.result.lowercase(Locale.getDefault()).contains(
                            constraint.toString()
                                .lowercase(Locale.getDefault())
                        )
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            submitList(filterResults?.values as MutableList<Code>)
        }

    }

    inner class CheatCodesViewHolder(private val binding: CheatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(code: Code) {
            binding.apply {
                itemCode.text = code.code
                itemEffect.text = code.result
            }
        }

    }

    companion object {
        private val CHEAT_CODES_COMPARATOR = object : DiffUtil.ItemCallback<Code>() {
            override fun areItemsTheSame(
                oldItem: Code,
                newItem: Code
            ) =
                oldItem.code == newItem.code

            override fun areContentsTheSame(
                oldItem: Code,
                newItem: Code
            ) =
                oldItem == newItem

        }
    }

    override fun getFilter(): Filter {
        return customFilter
    }


}