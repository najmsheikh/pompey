package me.najmsheikh.pompey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/25/21.
 */
class MediaContentSearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = MediaContentSearchFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
    }

    companion object {

        fun createLaunchIntent(context: Context): Intent {
            return Intent(context, MediaContentSearchActivity::class.java)
        }

    }

}