package me.najmsheikh.pompey

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow

/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(media: Media): PlaybackVideoFragment {
            val fragment = PlaybackVideoFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(ARG_MEDIA, media)
            }
            return fragment
        }
    }

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private lateinit var playerAdapter: MediaPlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (_, title, description, _, _, videoUrl) = arguments?.getSerializable(ARG_MEDIA) as Media

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        playerAdapter = MediaPlayerAdapter(context)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        transportControlGlue.host = glueHost
        transportControlGlue.title = title
        transportControlGlue.subtitle = description
        transportControlGlue.isSeekEnabled = true
        transportControlGlue.playWhenPrepared()

        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
        playerAdapter.setDataSource(Uri.parse(videoUrl))
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }
}