package me.najmsheikh.pompey

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import me.najmsheikh.pompey.data.models.MediaContent
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.LONG

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: ViewHolder,
        item: Any,
    ) {
        val media = item as MediaContent

        viewHolder.title.text = media.title
        viewHolder.subtitle.text =
            media.releaseDate?.format(DateTimeFormatter.ofLocalizedDate(LONG))
        viewHolder.body.text = media.description
    }
}