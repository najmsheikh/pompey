package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Details activity class that loads [VideoDetailsFragment] class.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            val media = intent.extras?.getSerializable(EXTRA_MEDIA) as Media
            val fragment = VideoDetailsFragment.newInstance(media)

            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commitNow();
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        private const val EXTRA_MEDIA = "media"

        fun createLaunchIntent(context: Context, media: Media): Intent {
            return Intent(context, DetailsActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, media)
            }
        }
    }
}