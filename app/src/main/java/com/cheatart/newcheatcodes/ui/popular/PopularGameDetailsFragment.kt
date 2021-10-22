package com.cheatart.newcheatcodes.ui.popular

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.cheatart.newcheatcodes.db.Game
import com.cheatart.newcheatcodes.model.GameData
import com.cheatart.newcheatcodes.model.ScreenShot
import com.cheatart.newcheatcodes.model.Stores
import com.cheatart.newcheatcodes.other.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL


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
    private var favorite = false
    private var gameData: GameData? = null
    private var seekBar: CustomSeekBar? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPopularGameDetailsBinding.bind(view)
        setHasOptionsMenu(true)
        seekBar = view.findViewById(R.id.progress_bar)
        seekBar!!.isEnabled = false
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        sliderAdapter = SliderAdapter(this)
        storesAdapter = StoresAdapter(this)
        viewModel.getGameData(args.id)
        viewModel.gameDetailsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    gameData = it.value
                    viewModel.getScreenShots(gameData!!.id!!)
                    addStoreImages(it.value)
                    bind(binding, it.value)
                    storesAdapter.submitList(it.value.stores)
                    initRatings()
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                }
            }
        }
        initViewPager(binding)
        initStoresRecyclerView(binding)

        viewModel.getAllFavIds().forEach {
            if (it == args.id) {
                favorite = true
                initFavButton(favorite)
            }
        }


        binding.apply {
            addToFavorites.setOnClickListener { addGameToFavorites() }
            shareWithFriends.setOnClickListener { shareGameWithFriends() }

        }

        viewModel.screenShotsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    sliderAdapter.setClip(gameData?.clip?.clip)
                    sliderAdapter.submitList(it.value.results)
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                }
            }
        }
    }

    private fun shareGameWithFriends() {
        LoadingScreen.displayLoading(requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val imageUri = requireContext().getImageUri(
                BitmapFactory.decodeStream(
                    URL(gameData?.background_image).openConnection().getInputStream()
                ),
                gameData?.name
            )
            val extraText = "${gameData?.name}\n" +
                    "Release date: ${gameData?.released}\n" +
                    "Genre: ${gameData?.genres?.joinToString(separator = ", ") { it.name.toString() }}\n" +
                    "Stores: ${gameData?.stores?.joinToString(separator = " ,\n") { "${it.store?.name} ${it.url}" }}\n" +
                    "Developer: ${gameData?.developers?.joinToString(separator = ", ") { it.name.toString() }}\n" +
                    "Publisher: ${gameData?.publishers?.joinToString(separator = ", ") { it.name.toString() }}\n" +
                    "Platforms: ${gameData?.platforms?.joinToString(separator = ", ") { it.platform?.name.toString() }}\n" +
                    "Youtube: ${gameData?.clip?.video}"
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, extraText)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = "image/jpeg"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share with"))
            LoadingScreen.hideLoading()
        }
    }

    private fun initRatings() {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        gameData?.ratings?.forEachIndexed { _, rating ->
            when (rating.title) {
                "exceptional" -> {
                    val view =
                        inflater.inflate(R.layout.rating_item, binding.constraintLayout, false)
                    view.layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    val ratingName = view.findViewById<TextView>(R.id.rating_name)
                    val ratingCount = view.findViewById<TextView>(R.id.rating_count)
                    view.id = View.generateViewId()
                    ratingName.text = rating.title.replaceFirstChar(transform = Char::titlecase)
                    TextViewCompat.setCompoundDrawableTintList(
                        ratingName,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.exceptional_color
                            )
                        )
                    )
                    ratingCount.text = rating.count.toString()
                    binding.constraintLayout.addView(view)
                    binding.flow.addView(view)
                    rating.color =
                        ContextCompat.getColor(requireContext(), R.color.exceptional_color)
                }
                "recommended" -> {
                    val view =
                        inflater.inflate(R.layout.rating_item, binding.constraintLayout, false)
                    view.layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    val ratingName = view.findViewById<TextView>(R.id.rating_name)
                    val ratingCount = view.findViewById<TextView>(R.id.rating_count)
                    view.id = View.generateViewId()
                    ratingName.text = rating.title.replaceFirstChar(transform = Char::titlecase)
                    TextViewCompat.setCompoundDrawableTintList(
                        ratingName,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.recommended_color
                            )
                        )
                    )
                    ratingCount.text = rating.count.toString()
                    binding.constraintLayout.addView(view)
                    binding.flow.addView(view)
                    rating.color =
                        ContextCompat.getColor(requireContext(), R.color.recommended_color)
                }
                "meh" -> {
                    val view =
                        inflater.inflate(R.layout.rating_item, binding.constraintLayout, false)
                    view.layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    val ratingName = view.findViewById<TextView>(R.id.rating_name)
                    val ratingCount = view.findViewById<TextView>(R.id.rating_count)
                    view.id = View.generateViewId()
                    ratingName.text = rating.title.replaceFirstChar(transform = Char::titlecase)
                    TextViewCompat.setCompoundDrawableTintList(
                        ratingName,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.meh_color
                            )
                        )
                    )
                    ratingCount.text = rating.count.toString()
                    binding.constraintLayout.addView(view)
                    binding.flow.addView(view)
                    rating.color = ContextCompat.getColor(requireContext(), R.color.meh_color)
                }
                "skip" -> {
                    val view =
                        inflater.inflate(R.layout.rating_item, binding.constraintLayout, false)
                    view.layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    val ratingName = view.findViewById<TextView>(R.id.rating_name)
                    val ratingCount = view.findViewById<TextView>(R.id.rating_count)
                    view.id = View.generateViewId()
                    ratingName.text = rating.title.replaceFirstChar(transform = Char::titlecase)
                    TextViewCompat.setCompoundDrawableTintList(
                        ratingName,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.skip_color
                            )
                        )
                    )
                    ratingCount.text = rating.count.toString()
                    binding.constraintLayout.addView(view)
                    binding.flow.addView(view)
                    rating.color = ContextCompat.getColor(requireContext(), R.color.skip_color)
                }

            }
        }
        binding.flow.setWrapMode(Flow.WRAP_ALIGNED)
        seekBar!!.initData(gameData!!.ratings)
        seekBar!!.invalidate()
    }


    private fun addGameToFavorites() {
        val id = gameData?.id
        val gameName = gameData?.name
        val gameImage = gameData?.background_image
        val game = Game(id, gameName, gameImage)
        favorite = if (favorite) {
            viewModel.deleteGame(game)
            binding.root.makeSnackbar("Removed from favorites")
            false
        } else {
            viewModel.insertGame(game)
            binding.root.makeSnackbar("Added to favorites")
            true
        }
        initFavButton(favorite)
    }

    private fun initFavButton(favorite: Boolean) {
        if (favorite) {
            binding.addToFavorites.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.remove_from_favorites_bg)
            binding.favHeart.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transfer_green
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            );
            binding.favAddTo.text = "Added to"
        } else {
            binding.addToFavorites.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.add_to_favorites_bg)
            binding.favHeart.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.white),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
            binding.favAddTo.text = "Add to"
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
                game.platforms?.forEach { platform ->
                    val platformName = platform.platform?.name
                    val textView = TextView(requireContext())
                    textView.setClickableText(
                        platformName,
                        ContextCompat.getColor(requireContext(), R.color.white),
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                val action =
                                    PopularGameDetailsFragmentDirections.actionPopularGameDetailsFragmentToSearchFragment(
                                        platform.platform?.id!!,
                                        "platform"
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    )
                    this.addView(textView)
                }
            }
            detailsGenre.apply {
                game.genres?.forEach { genre ->
                    val genreName = genre.name
                    val textView = TextView(requireContext())
                    textView.setClickableText(
                        genreName,
                        ContextCompat.getColor(requireContext(), R.color.white),
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                val action =
                                    PopularGameDetailsFragmentDirections.actionPopularGameDetailsFragmentToSearchFragment(
                                        genre.id!!,
                                        "genre"
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    )
                    this.addView(textView)
                }
            }
            detailsAgeRating.text = game.esrbRating?.name ?: "Not rated"
            description.text = game.description.toString()
            detailsGameWebsite.setClickableText(
                game.website,
                ContextCompat.getColor(requireContext(), R.color.white),
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.website))
                        startActivity(intent)
                    }

                }
            )
            detailsMetascore.text = game.metacritic.toString()
            detailsReleaseDateTextview.text = game.released
            detailsPlaytime.append("${game.playtime} hours")
            game.developers?.forEach { developer ->
                binding.detailsDeveloper.append("${developer.name}\n")
            }
        }
    }

    private fun initViewPager(binding: FragmentPopularGameDetailsBinding) {
        binding.detailsViewPager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - kotlin.math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            setPageTransformer(compositePageTransformer)
            adapter = sliderAdapter
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

    override fun onScreenshotItemClick(screenShot: ScreenShot, isVideo: Boolean) {
        showImageDialog(
            requireContext(),
            layoutInflater,
            sliderAdapter.currentList,
            screenShot,
            isVideo
        )
    }

    private fun showImageDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        screenShots: List<ScreenShot>,
        screenShot: ScreenShot,
        isVideo: Boolean
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
        viewPager.apply {
            clipToPadding = false
            clipChildren = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = DialogSliderAdapter(screenShots, gameData?.clip, requireContext())
        }
        springDotsIndicator.setViewPager2(viewPager)
        if (isVideo) {
            viewPager.currentItem = 0
        } else {
            viewPager.currentItem = screenShots.indexOf(screenShot)
        }
        dialog.apply {
            setContentView(dialogView)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStoreItemClick(store: Stores?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(store!!.url))
        startActivity(intent)
    }

}