package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.najmsheikh.pompey.data.models.Video

/**
 * Details activity class that loads [VideoDetailsFragment] class.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            val video = intent.extras?.getParcelable(EXTRA_MEDIA) as Video
            val fragment = VideoDetailsFragment.newInstance(video)

            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commitNow()
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        private const val EXTRA_MEDIA = "media"

        fun createLaunchIntent(context: Context, video: Video): Intent {
            return Intent(context, DetailsActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, video)
            }
        }
    }
}