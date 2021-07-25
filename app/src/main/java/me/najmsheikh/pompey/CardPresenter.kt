package me.najmsheikh.pompey

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.Movie
import me.najmsheikh.pompey.data.models.Show
import me.najmsheikh.pompey.data.models.Show.Season
import me.najmsheikh.pompey.data.models.Show.Season.Episode
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.LONG
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CardPresenter(
    private val cardWidth: Int = 313,
    private val cardHeight: Int = 470,
    private val shouldShowPoster: Boolean = true,
) : Presenter() {

    private var defaultPoster: Drawable? = null
    private var selectedBackgroundColor: Int by Delegates.notNull()
    private var defaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        defaultPoster = ContextCompat.getDrawable(parent.context, R.drawable.movie)
        selectedBackgroundColor =
            ContextCompat.getColor(parent.context, R.color.selected_background)
        defaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val media = item as MediaContent
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = media.title
        cardView.contentText = when (media) {
            is Movie -> viewHolder.view.context.getString(R.string.label_movie)
            is Show -> viewHolder.view.context.getString(R.string.label_show)
            is Season -> null
            is Episode -> media.releaseDate?.format(DateTimeFormatter.ofLocalizedDate(LONG))
        }
        cardView.setMainImageDimensions(cardWidth, cardHeight)
        if (shouldShowPoster) {
            Glide.with(viewHolder.view.context)
                .load(media.posterUrl)
                .centerCrop()
                .fitCenter()
                .error(defaultPoster)
                .into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

}
