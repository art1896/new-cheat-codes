package com.cheatart.newcheatcodes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.ItemAdsBinding
import com.cheatart.newcheatcodes.databinding.ListItemBinding
import com.cheatart.newcheatcodes.model.GameData
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.squareup.picasso.Picasso


private const val DEFAULT_VIEW_TYPE = 1
private const val NATIVE_AD_VIEW_TYPE = 2
private const val LIST_AD_DELTA = 11


class GameAdapter(
    private val listener: OnItemClickListener,
    private val context: Context
) :
    PagingDataAdapter<GameData, RecyclerView.ViewHolder>(GAME_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == DEFAULT_VIEW_TYPE) {
            val binding =
                ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GameViewHolder(binding)
        } else {
            val binding = ItemAdsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NativeAdViewHolder(binding)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DEFAULT_VIEW_TYPE) {
            try {
                val currentItem = getItem(getRealPosition(position))

                if (currentItem != null) {
                    (holder as GameAdapter.GameViewHolder).bind(currentItem)
                }
            } catch (e: IndexOutOfBoundsException) {

            }

        } else if (getItemViewType(position) == NATIVE_AD_VIEW_TYPE) {
            val adLoader = AdLoader.Builder(context, "ca-app-pub-1278927663267942/2884063224")
                .forNativeAd { ad: NativeAd ->
                    val templateView = (holder as GameAdapter.NativeAdViewHolder).adTemplate!!
                    val styles =
                        NativeTemplateStyle.Builder().build()

                    templateView.setStyles(styles)
                    templateView.setNativeAd(ad)

                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()


            adLoader.loadAd(AdRequest.Builder().build())
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (position > 0 && position % LIST_AD_DELTA == 0) {
            NATIVE_AD_VIEW_TYPE
        } else DEFAULT_VIEW_TYPE
    }


    private fun getRealPosition(position: Int): Int {
        return if (LIST_AD_DELTA == 0) {
            position
        } else {
            position - position / LIST_AD_DELTA
        }
    }

    inner class NativeAdViewHolder(binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var adTemplate: TemplateView? = null

        init {
            adTemplate = binding.myTemplate
        }


    }

    inner class GameViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(getRealPosition(position))
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(game: GameData) {
            binding.apply {
                Picasso.get().load(game.background_image).fit().centerCrop().into(posterImage)
                gameName.text = game.name
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(game: GameData)
    }


    companion object {
        private val GAME_COMPARATOR = object : DiffUtil.ItemCallback<GameData>() {
            override fun areItemsTheSame(oldItem: GameData, newItem: GameData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GameData, newItem: GameData) =
                oldItem == newItem


        }
    }

}