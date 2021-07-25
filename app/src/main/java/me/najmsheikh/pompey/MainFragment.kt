package me.najmsheikh.pompey

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiServiceGenerator
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.repository.MediaRepository

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseSupportFragment() {

    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var backgroundManager: BackgroundManager
    private var defaultBackground: Drawable? = null
    private var lastLoadedBackground: Drawable? = null
    private var backgroundLoadingJob: Job? = null

    private lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaRepository = MediaRepository(TmdbApiServiceGenerator.apiService)

        prepareBackgroundManager()
        setupUIElements()
        loadRows()
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        backgroundManager.drawable = lastLoadedBackground
    }

    private fun prepareBackgroundManager() {
        displayMetrics = requireContext().resources.displayMetrics
        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(requireActivity().window)
        defaultBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.header_background)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.search_opaque)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val trendingRowAdapter = ArrayObjectAdapter(cardPresenter)

        lifecycleScope.launchWhenCreated {
            val trendingMedia = mediaRepository.getAllTrendingContentForWeek()
            trendingRowAdapter.setItems(trendingMedia, null)
        }

        rowsAdapter.add(ListRow(HeaderItem(resources.getString(R.string.title_trending)),
            trendingRowAdapter))

        val gridHeader = HeaderItem(NUM_ROWS.toLong(), "PREFERENCES")

        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        gridRowAdapter.add(resources.getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(resources.getString(R.string.personal_settings))
        rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            val intent = VideoSearchActivity.createLaunchIntent(requireContext())
            startActivity(intent)
        }

        setOnItemViewSelectedListener { _, item, _, _ ->
            if (item is MediaContent) {
                enqueueLoadingBackground(item.backgroundUrl)
            }
        }

        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is MediaContent) {
                val intent = DetailsActivity.createLaunchIntent(requireContext(), item)
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    private fun enqueueLoadingBackground(backgroundImageUrl: String?) {
        backgroundLoadingJob?.cancel()
        backgroundLoadingJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(BACKGROUND_UPDATE_DELAY)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            val task = Glide.with(requireContext())
                .load(backgroundImageUrl)
                .centerCrop()
                .fitCenter()
                .error(defaultBackground)
                .submit(width, height)

            if (isActive) {
                backgroundManager.drawable = task.get()
                lastLoadedBackground = backgroundManager.drawable
            } else {
                task.cancel(true)
            }
        }
    }

    private inner class GridItemPresenter : Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
    }

    companion object {
        private val BACKGROUND_UPDATE_DELAY = 300L
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 6
        private val NUM_COLS = 15
    }
}