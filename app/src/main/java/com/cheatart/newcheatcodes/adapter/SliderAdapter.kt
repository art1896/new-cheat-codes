package com.cheatart.newcheatcodes.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.ClipItemBinding
import com.cheatart.newcheatcodes.databinding.SlideItemDetailsBinding
import com.cheatart.newcheatcodes.model.Clip
import com.cheatart.newcheatcodes.model.ScreenShot
import com.squareup.picasso.Picasso
import kotlin.math.ln


private const val IMAGE_VIEW_TYPE = 1
private const val VIDEO_VIEW_TYPE = 2

class SliderAdapter(
    private val onImageClickListener: OnItemClickListener
) :
    ListAdapter<ScreenShot, RecyclerView.ViewHolder>(SCREENSHOT_COMPARATOR),
    AdapterView.OnItemClickListener {


    private var clip: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIDEO_VIEW_TYPE) {
            val binding =
                ClipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VideoViewHolder(binding)
        } else {
            val binding =
                SlideItemDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SliderViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIDEO_VIEW_TYPE) {
            try {
                (holder as SliderAdapter.VideoViewHolder).bind()
            } catch (e: IndexOutOfBoundsException) {

            }
        } else if (getItemViewType(position) == IMAGE_VIEW_TYPE) {
            val currentItem = getItem(position)
            (holder as SliderAdapter.SliderViewHolder).bind(currentItem)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIDEO_VIEW_TYPE
        } else IMAGE_VIEW_TYPE
    }


    interface OnItemClickListener {
        fun onScreenshotItemClick(screenShot: ScreenShot, isVideo: Boolean)
    }

    inner class VideoViewHolder(private val binding: ClipItemBinding) :
        RecyclerView.ViewHolder(binding.root) {



        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    binding.videoView.pause()
                    onImageClickListener.onScreenshotItemClick(item, true)
                }
            }
        }

        fun bind() {
            clip?.let { clip ->
                binding.videoView.apply {
                    setVideoURI(Uri.parse(clip))
                    setOnPreparedListener {
                        it.setVolume(0f, 0f)
                        binding.toggle.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                val max = 100
                                val numerator: Double =
                                    if (max - 100 > 0) ln(max - 100.0) else 0.0
                                val volume = (1 - numerator / ln(max.toDouble())).toFloat()
                                it.setVolume(volume, volume)
                            } else {
                                it.setVolume(0f, 0f)
                            }
                        }
                        val videoRatio = it.videoWidth / it.videoHeight.toFloat()
                        val screenRatio = this.width / this.height.toFloat()
                        val scaleX = videoRatio / screenRatio
                        if (scaleX >= 1f) {
                            this.scaleX = scaleX
                        } else {
                            this.scaleY = 1f / scaleX
                        }
                        binding.toggle.isVisible = true
                        binding.spinKit.isVisible = false
                    }
                    setOnCompletionListener { binding.videoView.start() }
                    start()
                }
            }
        }

    }


    inner class SliderViewHolder(private val binding: SlideItemDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onImageClickListener.onScreenshotItemClick(item, false)
                }
            }
        }

        fun bind(screenShot: ScreenShot) {
            Picasso.get().load(screenShot.image).fit().centerCrop().into(binding.slideItemDetails)
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    fun setClip(clip: String?) {
        this.clip = clip
    }

    companion object {
        private val SCREENSHOT_COMPARATOR = object : DiffUtil.ItemCallback<ScreenShot>() {
            override fun areItemsTheSame(
                oldItem: ScreenShot,
                newItem: ScreenShot
            ) =
                oldItem.image == newItem.image

            override fun areContentsTheSame(
                oldItem: ScreenShot,
                newItem: ScreenShot
            ) =
                oldItem == newItem

        }
    }

}