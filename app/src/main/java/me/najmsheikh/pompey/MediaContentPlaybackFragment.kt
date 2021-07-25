package me.najmsheikh.pompey

import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import me.najmsheikh.pompey.data.models.MediaContent

/** Handles video playback with media controls. */
class MediaContentPlaybackFragment : VideoSupportFragment() {

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(media: MediaContent): MediaContentPlaybackFragment {
            val fragment = MediaContentPlaybackFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MEDIA, media)
            }
            return fragment
        }
    }

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private lateinit var playerAdapter: MediaPlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val media = arguments?.getParcelable(ARG_MEDIA) as MediaContent

        val glueHost = VideoSupportFragmentGlueHost(this@MediaContentPlaybackFragment)
        playerAdapter = MediaPlayerAdapter(context)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        transportControlGlue.host = glueHost
        transportControlGlue.title = media.title
        transportControlGlue.subtitle = media.description
        transportControlGlue.isSeekEnabled = true
        transportControlGlue.playWhenPrepared()

        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
//        playerAdapter.setDataSource(Uri.parse(videoUrl))
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }
}