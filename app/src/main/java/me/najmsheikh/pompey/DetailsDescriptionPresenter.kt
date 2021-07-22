package me.najmsheikh.pompey

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import me.najmsheikh.pompey.data.models.Video
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.LONG

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: ViewHolder,
        item: Any,
    ) {
        val video = item as Video

        viewHolder.title.text = video.title
        viewHolder.subtitle.text =
            video.releaseDate?.format(DateTimeFormatter.ofLocalizedDate(LONG))
        viewHolder.body.text = video.description
    }
}