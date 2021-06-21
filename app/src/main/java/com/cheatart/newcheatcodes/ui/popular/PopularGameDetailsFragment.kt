package com.cheatart.newcheatcodes.ui.popular

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.DialogSliderAdapter
import com.cheatart.newcheatcodes.adapter.SliderAdapter
import com.cheatart.newcheatcodes.adapter.StoresAdapter
import com.cheatart.newcheatcodes.data.network.Resource
import com.cheatart.newcheatcodes.databinding.FragmentPopularGameDetailsBinding
import com.cheatart.newcheatcodes.model.GameData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularGameDetailsFragment : Fragment(R.layout.fragment_popular_game_details),
    SliderAdapter.OnItemClickListener,
    StoresAdapter.OnStoresItemClickListener {

    private val args by navArgs<PopularGameDetailsFragmentArgs>()
    private val viewModel by viewModels<DetailsViewModel>()

    private var _binding: FragmentPopularGameDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var storesAdapter: StoresAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPopularGameDetailsBinding.bind(view)

        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        sliderAdapter = SliderAdapter(this)
        storesAdapter = StoresAdapter(this)
        addStoreImages(args.game)
        bind(binding, args.game)
        initViewPager(binding)
        initStoresRecyclerView(binding)
        viewModel.getScreenShots(args.game.id!!)
        viewModel.getGameDevelopers(args.game.id!!)


        viewModel.getGameStores(args.game.id!!, args.game).observe(viewLifecycleOwner) {
            storesAdapter.submitList(it)
        }

        viewModel.developersResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.value.developers.forEach { developer ->
                        binding.detailsDeveloper.append("${developer.name}\n")
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                }
            }
        }


        viewModel.screenShotsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    sliderAdapter.submitList(it.value.results)
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                }
            }
        }

    }

    private fun initStoresRecyclerView(binding: FragmentPopularGameDetailsBinding) {
        binding.storesRecycler.adapter = storesAdapter
    }

    private fun addStoreImages(game: GameData) {
        game.stores?.forEach {
            when (it.store?.name) {
                getString(R.string.store_app_store) -> it.store.image =
                    R.drawable.ic_store_app_store
                getString(R.string.store_epic) -> it.store.image =
                    R.drawable.ic_store_epic_games
                getString(R.string.store_google_play) -> it.store.image =
                    R.drawable.ic_store_google_play
                getString(R.string.store_nintendo) -> it.store.image =
                    R.drawable.ic_store_nintendo
                getString(R.string.store_ps) -> it.store.image =
                    R.drawable.ic_playstation_bbottom
                getString(R.string.store_steam) -> it.store.image =
                    R.drawable.ic_store_steam
                getString(R.string.store_xbox) -> it.store.image =
                    R.drawable.ic_store_xbox
                getString(R.string.store_xbox360) -> it.store.image =
                    R.drawable.ic_store_xbox
                getString(R.string.store_gog) -> it.store.image =
                    R.drawable.ic_store_gog
                getString(R.string.store_itch) -> it.store.image =
                    R.drawable.ic_store_itch
            }
        }
    }


    private fun bind(binding: FragmentPopularGameDetailsBinding, game: GameData) {
        binding.apply {
            Picasso.get().load(game.background_image).fit().centerCrop().into(detailsBackground)
            setPlatforms(game, this)
            detailsGameName.text = game.name
            detailsReleaseDate.text = game.released
            detailsPlatform.apply {
                game.platforms?.forEach {
                    val platformName = it.platform?.name
                    this.append("$platformName\n")
                }
            }
            detailsGenre.apply {
                game.genres?.forEach {
                    val genreName = it.name
                    this.append("$genreName\n")
                }
            }
            detailsMetascore.text = game.metacritic.toString()
            detailsReleaseDateTextview.text = game.released
            detailsPlaytime.append("${game.playtime} hours")
        }
    }

    private fun initViewPager(binding: FragmentPopularGameDetailsBinding) {
        binding.detailsViewPager.apply {
            this.clipToPadding = false
            this.clipChildren = false
            this.offscreenPageLimit = 3
            this.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - kotlin.math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            this.setPageTransformer(compositePageTransformer)
            this.adapter = sliderAdapter
        }
    }

    private fun setPlatforms(game: GameData, binding: FragmentPopularGameDetailsBinding) {
        game.platforms?.forEach {
            when {
                it.platform?.name?.substringBefore(" ") == getString(R.string.pc) -> {
                    binding.detailsWindowsPlatformImage.visibility = View.VISIBLE
                }
                it.platform?.name?.substringBefore(" ") == getString(R.string.playstation) -> {
                    binding.detailsPsPlatformImage.visibility = View.VISIBLE
                }
                it.platform?.name?.substringBefore(" ") == getString(R.string.xbox) -> {
                    binding.detailsXboxPlatformImage.visibility = View.VISIBLE
                }
                it.platform?.name?.substringBefore(" ") == getString(R.string.android) -> {
                    binding.detailsAndroidPlatformImage.visibility = View.VISIBLE
                }
                it.platform?.name?.substringBefore(" ") == getString(R.string.macos) -> {
                    binding.detailsIosPlatformImage.visibility = View.VISIBLE
                }
                it.platform?.name?.substringBefore(" ") == getString(R.string.linux) -> {
                    binding.detailsLinuxPlatformImage.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onScreenshotItemClick(screenShot: GameData.ScreenShot) {
        showImageDialog(requireContext(), layoutInflater, sliderAdapter.currentList, screenShot)
    }

    private fun showImageDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        screenShots: List<GameData.ScreenShot>,
        screenShot: GameData.ScreenShot
    ) {
        val dialog =
            Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        val dialogView = layoutInflater.inflate(R.layout.image_dialog, null)
        val viewPager = dialogView.findViewById<ViewPager2>(R.id.dialog_viewPager)
        val springDotsIndicator =
            dialogView.findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.dialog_close)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        viewPager.adapter = DialogSliderAdapter(screenShots)
        springDotsIndicator.setViewPager2(viewPager)
        viewPager.currentItem = screenShots.indexOf(screenShot)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStoreItemClick(store: GameData.Store?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(store?.url))
        startActivity(intent)
    }
}