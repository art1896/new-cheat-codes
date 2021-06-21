package com.cheatart.newcheatcodes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.SlideItemDetailsBinding
import com.cheatart.newcheatcodes.model.GameData
import com.squareup.picasso.Picasso

class SliderAdapter(
    private val onImageClickListener: OnItemClickListener
) :
    ListAdapter<GameData.ScreenShot, SliderAdapter.SliderViewHolder>(SCREENSHOT_COMPARATOR),
    AdapterView.OnItemClickListener {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        val binding =
            SlideItemDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

    }


    interface OnItemClickListener {
        fun onScreenshotItemClick(screenShot: GameData.ScreenShot)
    }

    inner class SliderViewHolder(private val binding: SlideItemDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onImageClickListener.onScreenshotItemClick(item)
                }
            }
        }

        fun bind(screenShot: GameData.ScreenShot) {
            Picasso.get().load(screenShot.image).fit().centerCrop().into(binding.slideItemDetails)
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    companion object {
        private val SCREENSHOT_COMPARATOR = object : DiffUtil.ItemCallback<GameData.ScreenShot>() {
            override fun areItemsTheSame(
                oldItem: GameData.ScreenShot,
                newItem: GameData.ScreenShot
            ) =
                oldItem.image == newItem.image

            override fun areContentsTheSame(
                oldItem: GameData.ScreenShot,
                newItem: GameData.ScreenShot
            ) =
                oldItem == newItem

        }
    }

}