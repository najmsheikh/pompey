package me.najmsheikh.pompey

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
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
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiServiceGenerator
import me.najmsheikh.pompey.data.models.Video
import me.najmsheikh.pompey.data.repository.VideoRepository
import kotlin.math.roundToInt

/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class VideoDetailsFragment : DetailsSupportFragment() {

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var rowAdapter: ArrayObjectAdapter
    private lateinit var videoRepository: VideoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val passedVideo = arguments?.getParcelable(ARG_MEDIA) as Video
        detailsBackground = DetailsSupportFragmentBackgroundController(this)
        presenterSelector = ClassPresenterSelector()
        rowAdapter = ArrayObjectAdapter(presenterSelector)
        videoRepository = VideoRepository(TmdbApiServiceGenerator.apiService)
        onItemViewClickedListener = ItemViewClickedListener()

        lifecycleScope.launchWhenCreated {
            val video = videoRepository.getMovieDetails(passedVideo.id) // TODO: Support shows

            setupDetailsOverviewRow(video)
            setupDetailsOverviewRowPresenter(video)
            setupRelatedContentRow(video)

            adapter = rowAdapter
            initializeBackground(video)
        }
    }

    private fun initializeBackground(video: Video) {
        detailsBackground.enableParallax()
        Glide.with(requireContext())
            .asBitmap()
            .centerCrop()
            .fitCenter()
            .error(R.drawable.default_background)
            .load(video.backgroundUrl)
            .into<CustomTarget<Bitmap>>(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    bitmap: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    detailsBackground.coverBitmap = bitmap
                    rowAdapter.notifyArrayItemRangeChanged(0, rowAdapter.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    detailsBackground.coverBitmap = null
                }
            })
    }

    private fun setupDetailsOverviewRow(video: Video) {
        val row = DetailsOverviewRow(video)
        row.imageDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        val width = convertDpToPixel(requireContext(), DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(requireContext(), DETAIL_THUMB_HEIGHT)
        Glide.with(requireContext())
            .load(video.posterUrl)
            .centerCrop()
            .fitCenter()
            .error(R.drawable.default_background)
            .into<CustomTarget<Drawable>>(object : CustomTarget<Drawable>(width, height) {
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

        val actionAdapter = ArrayObjectAdapter()

        actionAdapter.add(
            Action(
                ACTION_WATCH_TRAILER,
                resources.getString(R.string.watch_trailer_1),
                resources.getString(R.string.watch_trailer_2)
            )
        )
        actionAdapter.add(
            Action(
                ACTION_RENT,
                resources.getString(R.string.rent_1),
                resources.getString(R.string.rent_2)
            )
        )
        actionAdapter.add(
            Action(
                ACTION_BUY,
                resources.getString(R.string.buy_1),
                resources.getString(R.string.buy_2)
            )
        )
        row.actionsAdapter = actionAdapter

        rowAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter(video: Video) {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = ContextCompat.getColor(
            requireContext(), R.color.selected_background
        )

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
            activity, DetailsActivity.SHARED_ELEMENT_NAME
        )
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            if (action.id == ACTION_WATCH_TRAILER) {
                val intent = PlaybackActivity.createLaunchIntent(requireContext(), video)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), action.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupRelatedContentRow(video: Video) {
        val subcategories = arrayOf(getString(R.string.related_content))
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())

        video.recommendations.forEach(listRowAdapter::add)

        val header = HeaderItem(0, subcategories[0])
        rowAdapter.add(ListRow(header, listRowAdapter))
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is Video) {
                val intent = DetailsActivity.createLaunchIntent(requireContext(), item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder?.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                ).toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    companion object {
        private const val ACTION_WATCH_TRAILER = 1L
        private const val ACTION_RENT = 2L
        private const val ACTION_BUY = 3L

        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 274

        private const val ARG_MEDIA = "media"

        fun newInstance(video: Video): VideoDetailsFragment {
            val fragment = VideoDetailsFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MEDIA, video)
            }
            return fragment
        }
    }

}