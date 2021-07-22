package me.najmsheikh.pompey

import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import me.najmsheikh.pompey.data.models.Video

/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(video: Video): PlaybackVideoFragment {
            val fragment = PlaybackVideoFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MEDIA, video)
            }
            return fragment
        }
    }

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private lateinit var playerAdapter: MediaPlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val video = arguments?.getParcelable(ARG_MEDIA) as Video

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        playerAdapter = MediaPlayerAdapter(context)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        transportControlGlue.host = glueHost
        transportControlGlue.title = video.title
        transportControlGlue.subtitle = video.description
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