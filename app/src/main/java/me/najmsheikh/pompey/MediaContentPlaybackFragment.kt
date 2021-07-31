package me.najmsheikh.pompey

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.MediaContentSource

/** Handles video playback with media controls. */
class MediaContentPlaybackFragment : VideoSupportFragment(), Listener {

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
    private lateinit var subtitleView: SubtitleView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subtitleView = view.findViewById(R.id.lb_subtitles)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val media = arguments?.getParcelable(ARG_MEDIA) as MediaContent?
        val source = arguments?.getParcelable(ARG_SOURCE) as MediaContentSource?
        if (media == null || source == null) {
            activity?.finish()
            return
        }

        val renderersFactory = DefaultRenderersFactory(requireContext())
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
        exoPlayer = SimpleExoPlayer.Builder(requireContext(), renderersFactory).build()
        playerAdapter = LeanbackPlayerAdapter(requireContext(), exoPlayer as Player, 50)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter).apply {
            host = VideoSupportFragmentGlueHost(this@MediaContentPlaybackFragment)
            title = media.title
            subtitle = media.description
            isSeekEnabled = true
            playWhenPrepared()
        }

        exoPlayer?.playWhenReady = true
        exoPlayer?.addListener(this)
        exoPlayer?.prepare()

        val subtitles = source.subtitles.map { subtitleSource ->
            MediaItem.Subtitle(
                subtitleSource.source,
                MimeTypes.APPLICATION_SUBRIP,
                subtitleSource.language
            )
        }

        val mediaItem = MediaItem.Builder()
            .setUri(source.source)
            .setSubtitles(subtitles)
            .build()

        exoPlayer?.setMediaItem(mediaItem)
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.removeListener(this)
        exoPlayer?.release()
    }

    override fun onCues(cues: MutableList<Cue>) {
        subtitleView.onCues(cues)
    }

}