package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.MediaContentSource

/** Loads [MediaContentPlaybackFragment]. */
class MediaContentPlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val media = intent.extras?.getParcelable(EXTRA_MEDIA) as MediaContent?
            val source = intent.extras?.getParcelable(EXTRA_SOURCE) as MediaContentSource?
            val fragment = MediaContentPlaybackFragment.newInstance(media!!, source!!)

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    companion object {
        private const val EXTRA_MEDIA = "media"
        private const val EXTRA_SOURCE = "source"

        fun createLaunchIntent(
            context: Context,
            media: MediaContent,
            source: MediaContentSource,
        ): Intent {
            return Intent(context, MediaContentPlaybackActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, media)
                putExtra(EXTRA_SOURCE, source)
            }
        }
    }
}