package com.cheatart.newcheatcodes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.SlideItemBinding
import com.cheatart.newcheatcodes.databinding.VideoItemBinding
import com.cheatart.newcheatcodes.model.Clip
import com.cheatart.newcheatcodes.model.ScreenShot
import com.cheatart.newcheatcodes.other.getDisplayDimensions
import com.cheatart.newcheatcodes.other.toDp
import com.squareup.picasso.Picasso

private const val IMAGE_VIEW_TYPE = 1
private const val VIDEO_VIEW_TYPE = 2

class DialogSliderAdapter(
    private val sliderItems: List<ScreenShot>,
    private val clip: Clip?,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIDEO_VIEW_TYPE) {
            val binding =
                VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VideoViewHolder(binding)
        } else {
            val binding =
                SlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DialogSliderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == VIDEO_VIEW_TYPE) {
            (holder as VideoViewHolder).bind()
        } else if (getItemViewType(position) == IMAGE_VIEW_TYPE) {
            (holder as DialogSliderViewHolder).setImage(sliderItems[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIDEO_VIEW_TYPE
        } else IMAGE_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    inner class DialogSliderViewHolder(private val binding: SlideItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImage(sliderItem: ScreenShot) {
            Picasso.get().load(sliderItem.image).into(binding.imageSlide)
        }
    }

    inner class VideoViewHolder(private val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return false
                }
            }
            val webSettings: WebSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true
            val data =
                "<html><body style='margin:0;padding:0;'><iframe width=\"${context.getDisplayDimensions().first.toDp}\" height=\"${((context.getDisplayDimensions().second) / 2).toDp}\" src=\"https://www.youtube.com/embed/${clip?._video}\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>"
            binding.webView.loadData(data, "text/html", "utf-8")
        }
    }


}