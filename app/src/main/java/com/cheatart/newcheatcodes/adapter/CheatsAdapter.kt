package com.cheatart.newcheatcodes.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheatart.newcheatcodes.databinding.GamesGreedItemBinding
import com.cheatart.newcheatcodes.databinding.ItemAdsBinding
import com.cheatart.newcheatcodes.model.CheatModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.squareup.picasso.Picasso
import java.util.*

private const val DEFAULT_VIEW_TYPE = 1
private const val NATIVE_AD_VIEW_TYPE = 2
private const val LIST_AD_DELTA = 7


class CheatsAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val context: Context
) :
    ListAdapter<CheatModel, RecyclerView.ViewHolder>(CHEATS_COMPARATOR), Filterable {

    private var list = mutableListOf<CheatModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == DEFAULT_VIEW_TYPE) {
            val binding =
                GamesGreedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CheatsViewHolder(binding)
        } else {
            val binding = ItemAdsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NativeAdViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DEFAULT_VIEW_TYPE) {
            try {
                val currentItem = getItem(getRealPosition(position))
                (holder as CheatsViewHolder).bind(currentItem)
            } catch (e: IndexOutOfBoundsException) {

            }

        } else if (getItemViewType(position) == NATIVE_AD_VIEW_TYPE) {
            val adLoader = AdLoader.Builder(context, "ca-app-pub-1278927663267942/1280193746")
                .forNativeAd { ad: NativeAd ->
                    val templateView = (holder as NativeAdViewHolder).adTemplate!!
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


    fun setData(list: MutableList<CheatModel>?) {
        this.list = list!!
        submitList(list)
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    override fun getItemCount(): Int {
        var additionalContent = 0
        if (currentList.size > 0 && LIST_AD_DELTA > 0 && currentList.size > LIST_AD_DELTA) {
            additionalContent =
                (currentList.size + (currentList.size / LIST_AD_DELTA)) / LIST_AD_DELTA
        }
        return currentList.size + additionalContent
    }

    private fun getRealPosition(position: Int): Int {
        return if (LIST_AD_DELTA == 0) {
            position
        } else {
            position - position / LIST_AD_DELTA
        }
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<CheatModel>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(list)
            } else {
                for (item in list) {
                    if (item.name.lowercase(Locale.getDefault()).contains(
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
            submitList(filterResults?.values as MutableList<CheatModel>)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(cheat: CheatModel)
    }

    inner class NativeAdViewHolder(binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var adTemplate: TemplateView? = null

        init {
            adTemplate = binding.myTemplate
        }


    }


    inner class CheatsViewHolder(private val binding: GamesGreedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(getRealPosition(position))
                    onItemClickListener.onItemClick(item)
                }
            }
        }


        fun bind(cheat: CheatModel) {
            binding.apply {
                Picasso.get().load(cheat.poster).into(posterImage)
                gameName.text = cheat.name
                cheatCount.text = cheat.codes.size.toString()
            }
        }


    }


    companion object {
        private val CHEATS_COMPARATOR = object : DiffUtil.ItemCallback<CheatModel>() {
            override fun areItemsTheSame(
                oldItem: CheatModel,
                newItem: CheatModel
            ) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: CheatModel,
                newItem: CheatModel
            ) =
                oldItem == newItem

        }
    }
}