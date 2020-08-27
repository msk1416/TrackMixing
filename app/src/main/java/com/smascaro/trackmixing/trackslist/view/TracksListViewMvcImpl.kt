package com.smascaro.trackmixing.trackslist.view

import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListViewMvcImpl @Inject constructor(
    private val tracksListAdapter: TracksListAdapter
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc {

    private lateinit var mRecyclerViewTracks: RecyclerView

    private var currentDataSource: TracksListViewMvc.TracksDataSource =
        TracksListViewMvc.TracksDataSource.DATABASE

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        mRecyclerViewTracks.layoutManager = LinearLayoutManager(getContext())
        (mRecyclerViewTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mRecyclerViewTracks.setHasFixedSize(true)
        tracksListAdapter.setOnTrackClickedListener(this)
        mRecyclerViewTracks.adapter = this.tracksListAdapter
        val fab = findViewById<FloatingActionButton>(R.id.fabTempMode)
        fab.setOnClickListener {
//            currentDataSource = when (currentDataSource) {
//                TracksListViewMvc.TracksDataSource.DATABASE -> TracksListViewMvc.TracksDataSource.SERVER
//                TracksListViewMvc.TracksDataSource.SERVER -> TracksListViewMvc.TracksDataSource.DATABASE
//            }
//            getListeners().forEach {
//                it.onCurrentDataSourceRequest(currentDataSource)
//            }
            getListeners().forEach {
                it.onSearchNavigationButtonClicked()
            }
        }

    }

    override fun bindTracks(tracks: List<Track>) {
        this.tracksListAdapter.bindTracks(tracks)
    }

    override fun getCurrentDataSource(): TracksListViewMvc.TracksDataSource {
        return currentDataSource
    }

    override fun onTrackClicked(
        track: Track,
        card: MaterialCardView
    ) {
        val title = card.findViewById<MaterialTextView>(R.id.trackTitle)
        val imageView = card.findViewById<ImageView>(R.id.thumbnailImg)
        val extras = FragmentNavigatorExtras(
            card to track.videoKey,
            title to track.title
        )
        getListeners().forEach {
            it.onTrackClicked(track)
        }
    }


}