package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.najmsheikh.pompey.data.models.Video

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val video = intent.extras?.getParcelable(EXTRA_MEDIA) as Video
            val fragment = PlaybackVideoFragment.newInstance(video)

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    companion object {
        private const val EXTRA_MEDIA = "media"

        fun createLaunchIntent(context: Context, video: Video): Intent {
            return Intent(context, PlaybackActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, video)
            }
        }
    }
}