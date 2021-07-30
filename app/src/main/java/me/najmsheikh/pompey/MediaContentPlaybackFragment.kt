package me.najmsheikh.pompey

import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.util.EventLogger
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.MediaContentSource

/** Handles video playback with media controls. */
class MediaContentPlaybackFragment : VideoSupportFragment() {

    companion object {
        private const val ARG_MEDIA = "media"
        private const val ARG_SOURCE = "source"

        fun newInstance(
            media: MediaContent,
            source: MediaContentSource,
        ): MediaContentPlaybackFragment {
            val fragment = MediaContentPlaybackFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MEDIA, media)
                putParcelable(ARG_SOURCE, source)
            }
            return fragment
        }
    }

    private var exoPlayer: SimpleExoPlayer? = null
    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var playerAdapter: LeanbackPlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val media = arguments?.getParcelable(ARG_MEDIA) as MediaContent?
        val source = arguments?.getParcelable(ARG_SOURCE) as MediaContentSource?
        if (media == null || source == null) {
            activity?.finish()
            return
        }

        val glueHost = VideoSupportFragmentGlueHost(this@MediaContentPlaybackFragment)
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        exoPlayer?.addAnalyticsListener(EventLogger(null))
        playerAdapter = LeanbackPlayerAdapter(requireContext(), exoPlayer as Player, 50)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        transportControlGlue.host = glueHost
        transportControlGlue.title = media.title
        transportControlGlue.subtitle = media.description
        transportControlGlue.isSeekEnabled = true
        transportControlGlue.playWhenPrepared()
        exoPlayer?.playWhenReady = true

        val mediaItem = MediaItem.Builder()
            .setUri(source.source)
            .build()

        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
    }
}