package me.najmsheikh.pompey

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val movie = item as Media

        viewHolder.title.text = movie.title
        viewHolder.subtitle.text = movie.studio
        viewHolder.body.text = movie.description
    }
}