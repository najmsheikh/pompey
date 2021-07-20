package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val media = intent.extras?.getSerializable(EXTRA_MEDIA) as Media
            val fragment = PlaybackVideoFragment.newInstance(media)

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    companion object {
        private const val EXTRA_MEDIA = "media"

        fun createLaunchIntent(context: Context, media: Media): Intent {
            return Intent(context, PlaybackActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, media)
            }
        }
    }
}