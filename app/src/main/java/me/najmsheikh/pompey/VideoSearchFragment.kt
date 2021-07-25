package me.najmsheikh.pompey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SearchBar
import androidx.leanback.widget.SpeechOrbView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiServiceGenerator
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.repository.MediaRepository

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/25/21.
 */
class VideoSearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var cardPresenter: CardPresenter
    private lateinit var resultRowAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaRepository = MediaRepository(TmdbApiServiceGenerator.apiService)
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        cardPresenter = CardPresenter()
        resultRowAdapter = ArrayObjectAdapter(cardPresenter)
        rowsAdapter.add(ListRow(resultRowAdapter))

        setSearchResultProvider(this)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        val searchFrame = root?.findViewById<FrameLayout>(androidx.leanback.R.id.lb_search_frame)
        val searchBar = searchFrame?.findViewById<SearchBar>(androidx.leanback.R.id.lb_search_bar)
        val speechOrb =
            searchBar?.findViewById<SpeechOrbView>(androidx.leanback.R.id.lb_search_bar_speech_orb)

        // Hide microphone icon, prefer keyboard TTS instead
        speechOrb?.visibility = View.GONE

        return root
    }

    override fun getResultsAdapter() = rowsAdapter

    override fun onQueryTextChange(newQuery: String?) = false

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrBlank()) return false
        lifecycleScope.launch {
            val results = mediaRepository.searchForContent(query)
            resultRowAdapter.setItems(results, null)
        }
        return true
    }

    companion object {

        fun newInstance(): VideoSearchFragment {
            return VideoSearchFragment()
        }

    }
}