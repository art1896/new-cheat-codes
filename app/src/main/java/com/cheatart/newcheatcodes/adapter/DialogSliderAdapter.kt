package com.cheatart.newcheatcodes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.SlideItemBinding
import com.cheatart.newcheatcodes.model.GameData
import com.squareup.picasso.Picasso

class DialogSliderAdapter(
    private val sliderItems: List<GameData.ScreenShot>
) :
    RecyclerView.Adapter<DialogSliderAdapter.DialogSliderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DialogSliderViewHolder {
        val binding =
            SlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogSliderViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: DialogSliderAdapter.DialogSliderViewHolder,
        position: Int
    ) {
        holder.setImage(sliderItems[position])
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    inner class DialogSliderViewHolder(private val binding: SlideItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImage(sliderItem: GameData.ScreenShot) {
            Picasso.get().load(sliderItem.image).into(binding.imageSlide)
        }


    }


}