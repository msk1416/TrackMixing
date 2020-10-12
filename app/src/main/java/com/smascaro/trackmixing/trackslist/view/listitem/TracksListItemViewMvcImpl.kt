package com.smascaro.trackmixing.trackslist.view.listitem

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListItemViewMvcImpl @Inject constructor(
    val glide: RequestManager
) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(),
    TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackAuthorTxt: TextView
    private lateinit var mTrackDuration: TextView
    private lateinit var mTrackState: TextView
    private lateinit var mTrackThumbnailImg: ImageView
    private var mPosition: Int = -1

    override fun initialize() {
        super.initialize()
        mTrackTitleTxt = findViewById(R.id.tv_item_track_title)
        mTrackThumbnailImg = findViewById(R.id.iv_item_track_thumbnail)
        mTrackAuthorTxt = findViewById(R.id.tv_item_track_author)
        mTrackDuration = findViewById(R.id.tv_item_track_duration)
        mTrackState = findViewById(R.id.tv_item_track_status)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        getRootView().setOnClickListener {
            getListeners().forEach {
                it.onTrackClicked(mTrack)
            }
        }

        getRootView().setOnLongClickListener {
            it.isSelected = true
            true
        }
    }

    override fun bindTrack(track: Track) {
        mTrack = track
        mTrackTitleTxt.text = track.title
        mTrackAuthorTxt.text = track.author
        mTrackDuration.text =
            TimeHelper.fromSeconds(track.secondsLong.toLong()).toStringRepresentation()
        mTrackState.text = "Downloaded"
        glide
            .asBitmap()
            .load(mTrack.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(mTrackThumbnailImg)
        mTrackTitleTxt.transitionName = track.title
    }

    override fun bindPosition(position: Int) {
        mPosition = position
    }
}