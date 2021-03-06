package me.najmsheikh.pompey

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.najmsheikh.pompey.data.api.opensubs.OpenSubtitlesApiServiceGenerator
import me.najmsheikh.pompey.data.api.sourcerer.SourcererApiServiceGenerator
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiServiceGenerator
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.MediaContentSource
import me.najmsheikh.pompey.data.models.Movie
import me.najmsheikh.pompey.data.models.Show
import me.najmsheikh.pompey.data.models.Show.Season
import me.najmsheikh.pompey.data.models.Show.Season.Episode
import me.najmsheikh.pompey.data.repository.MediaRepository
import me.najmsheikh.pompey.data.repository.SourceRepository
import kotlin.math.roundToInt

/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class MediaContentDetailsFragment : DetailsSupportFragment() {

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var rowAdapter: ArrayObjectAdapter
    private lateinit var episodeRowAdapter: ArrayObjectAdapter
    private lateinit var mediaRepository: MediaRepository
    private lateinit var sourceRepository: SourceRepository

    private var seasonLoadingJob: Job? = null

    private val detailPosterWidth by lazy { convertDpToPixel(274) }
    private val detailPosterHeight by lazy { convertDpToPixel(274) }

    private val seasonButtonWidth by lazy { convertDpToPixel(125) }
    private val seasonButtonHeight by lazy { convertDpToPixel(15) }

    private val episodePosterWidth by lazy { convertDpToPixel(285) }
    private val episodePosterHeight by lazy { convertDpToPixel(160) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val passedMedia = arguments?.getParcelable(ARG_MEDIA) as MediaContent?
        detailsBackground = DetailsSupportFragmentBackgroundController(this)
        presenterSelector = ClassPresenterSelector()
        rowAdapter = ArrayObjectAdapter(presenterSelector)
        mediaRepository = MediaRepository(TmdbApiServiceGenerator.apiService)
        sourceRepository = SourceRepository(
            SourcererApiServiceGenerator.apiService,
            OpenSubtitlesApiServiceGenerator.apiService,
        )
        onItemViewClickedListener = ItemViewClickedListener()

        lifecycleScope.launchWhenCreated {
            val media = when (passedMedia) {
                is Movie -> mediaRepository.getMovieDetails(passedMedia.id)
                is Show -> mediaRepository.getShowDetails(passedMedia.id)
                is Episode -> mediaRepository.getEpisodeDetails(
                    passedMedia.showId,
                    passedMedia.showImdbId,
                    passedMedia.seasonNumber,
                    passedMedia.episodeNumber
                )
                else -> null
            }

            if (media == null) {
                activity?.finish()
                return@launchWhenCreated
            }

            val sources = when (media) {
                is Movie -> media.imdbId?.let { imdbId ->
                    sourceRepository.getMovieSources(imdbId)
                }
                is Episode -> media.showImdbId?.let { imdbId ->
                    sourceRepository.getEpisodeSources(
                        imdbId,
                        media.seasonNumber,
                        media.episodeNumber
                    )
                }
                else -> null
            } ?: emptyList()
            val sourceMap = sources.associateBy { it.hashCode().toLong() }

            setupDetailsOverviewRow(media, sources)
            setupDetailsOverviewRowPresenter(media, sourceMap)
            setupSeasonsContentRow(media)
            setupEpisodesContentRow(media)
            setupRelatedContentRow(media)

            adapter = rowAdapter
            initializeBackground(media)
        }
    }

    private fun initializeBackground(media: MediaContent) {
        val imageUrl = if (media is Episode) media.posterUrl else media.backgroundUrl
        Glide.with(requireContext())
            .asBitmap()
            .centerCrop()
            .fitCenter()
            .error(R.drawable.default_background)
            .load(imageUrl)
            .into<CustomTarget<Bitmap>>(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    bitmap: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    detailsBackground.coverBitmap = bitmap
                    detailsBackground.enableParallax()
                    rowAdapter.notifyArrayItemRangeChanged(0, rowAdapter.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    detailsBackground.coverBitmap = null
                }
            })
    }

    private fun setupDetailsOverviewRow(media: MediaContent, sources: List<MediaContentSource>) {
        val row = DetailsOverviewRow(media)

        if (media !is Episode) {
            Glide.with(requireContext())
                .load(media.posterUrl)
                .centerCrop()
                .fitCenter()
                .error(R.drawable.default_background)
                .into<CustomTarget<Drawable>>(object :
                    CustomTarget<Drawable>(detailPosterWidth, detailPosterHeight) {
                    override fun onResourceReady(
                        drawable: Drawable,
                        transition: Transition<in Drawable>?,
                    ) {
                        row.imageDrawable = drawable
                        rowAdapter.notifyArrayItemRangeChanged(0, rowAdapter.size())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        row.imageDrawable = null
                    }
                })
        }

        val actionAdapter = ArrayObjectAdapter()

        sources.forEach { source ->
            actionAdapter.add(
                Action(
                    source.hashCode().toLong(),
                    resources.getString(R.string.action_watch),
                    source.title,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                )
            )
        }

        row.actionsAdapter = actionAdapter
        rowAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter(
        media: MediaContent,
        sourceMap: Map<Long, MediaContentSource>,
    ) {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = ContextCompat.getColor(
            requireContext(), R.color.selected_background
        )

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
            activity, MediaContentDetailsActivity.SHARED_ELEMENT_NAME
        )
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            val source = sourceMap[action.id]
            source?.let {
                val intent = MediaContentPlaybackActivity.createLaunchIntent(
                    context = requireContext(),
                    media = media,
                    source = source
                )
                startActivity(intent)
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupRelatedContentRow(media: MediaContent) {
        if (media.recommendations.isEmpty()) {
            return
        }

        val listRowAdapter = ArrayObjectAdapter(CardPresenter())
        listRowAdapter.setItems(media.recommendations, null)

        val header = HeaderItem(getString(R.string.heading_related_content))
        rowAdapter.add(ListRow(header, listRowAdapter))
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
    }

    private fun setupSeasonsContentRow(media: MediaContent) {
        if (media !is Show) {
            return
        }

        val listRowAdapter = ArrayObjectAdapter(CardPresenter(
            cardWidth = seasonButtonWidth,
            cardHeight = seasonButtonHeight,
            shouldShowPoster = false,
        ))

        listRowAdapter.setItems(media.seasons, null)
        loadSeasonDetails(media.seasons.first())

        val header = HeaderItem(getString(R.string.heading_seasons))
        rowAdapter.add(ListRow(header, listRowAdapter))
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
    }

    private fun setupEpisodesContentRow(media: MediaContent) {
        if (media !is Show) {
            return
        }

        episodeRowAdapter = ArrayObjectAdapter(CardPresenter(
            cardWidth = episodePosterWidth,
            cardHeight = episodePosterHeight,
        ))

        val header = HeaderItem(getString(R.string.heading_episodes))
        rowAdapter.add(ListRow(header, episodeRowAdapter))
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
    }

    private fun convertDpToPixel(dp: Int): Int {
        val density = requireContext().applicationContext.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    private fun viewMediaDetails(media: MediaContent, viewHolder: Presenter.ViewHolder?) {
        val intent = MediaContentDetailsActivity.createLaunchIntent(requireContext(), media)

        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            (viewHolder?.view as ImageCardView).mainImageView,
            MediaContentDetailsActivity.SHARED_ELEMENT_NAME
        ).toBundle()
        startActivity(intent, bundle)
    }

    private fun loadSeasonDetails(season: Season) {
        seasonLoadingJob?.cancel()
        seasonLoadingJob = lifecycleScope.launch {
            val fullSeason = mediaRepository.getSeasonDetails(
                showId = season.showId,
                showImdbId = season.showImdbId,
                seasonNumber = season.seasonNumber
            )
            episodeRowAdapter.setItems(fullSeason.episodes, null)
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row,
        ) {
            when (item) {
                is Movie -> viewMediaDetails(item, itemViewHolder)
                is Show -> viewMediaDetails(item, itemViewHolder)
                is Episode -> viewMediaDetails(item, itemViewHolder)
                is Season -> loadSeasonDetails(item)
            }
        }
    }

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(media: MediaContent): MediaContentDetailsFragment {
            val fragment = MediaContentDetailsFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MEDIA, media)
            }
            return fragment
        }
    }

}