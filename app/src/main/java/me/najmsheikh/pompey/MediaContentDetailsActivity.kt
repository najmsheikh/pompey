package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.najmsheikh.pompey.data.models.MediaContent

/**
 * Details activity class that loads [MediaContentDetailsFragment] class.
 */
class MediaContentDetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            val media = intent.extras?.getParcelable(EXTRA_MEDIA) as MediaContent
            val fragment = MediaContentDetailsFragment.newInstance(media)

            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commitNow()
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        private const val EXTRA_MEDIA = "media"

        fun createLaunchIntent(context: Context, media: MediaContent): Intent {
            return Intent(context, MediaContentDetailsActivity::class.java).apply {
                putExtra(EXTRA_MEDIA, media)
            }
        }
    }
}