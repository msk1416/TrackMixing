package com.smascaro.trackmixing.trackslist.controller


import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import com.smascaro.trackmixing.trackslist.business.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class TracksListController @Inject constructor(
    private val mFetchAvailableTracksUseCase: FetchAvailableTracksUseCase,
    private val mDownloadTrackUseCase: DownloadTrackUseCase,
    private val mFetchDownloadedTracks: FetchDownloadedTracks
) : BaseController<TracksListViewMvc>(),
    TracksListViewMvc.Listener,
    FetchAvailableTracksUseCase.Listener,
    DownloadTrackUseCase.Listener, FetchDownloadedTracks.Listener {
    override fun onTrackClicked(track: Track) {
        Timber.i("Track clicked: ${track.title}")
    }

    override fun onCurrentDataSourceRequest(dataSource: TracksListViewMvc.TracksDataSource) {
        loadTracksFrom(dataSource)
    }

    fun loadTracksFrom(dataSource: TracksListViewMvc.TracksDataSource) {
        if (dataSource == TracksListViewMvc.TracksDataSource.DATABASE) {
            mFetchDownloadedTracks.fetchTracksAndNotify(FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC)
        } else {
            mFetchAvailableTracksUseCase.fetchAvailableTracksAndNotify()
        }

    }

    fun onStart() {
        viewMvc.registerListener(this)
        mFetchAvailableTracksUseCase.registerListener(this)
        mDownloadTrackUseCase.registerListener(this)
        mFetchDownloadedTracks.registerListener(this)
        loadTracksFrom(viewMvc.getCurrentDataSource())
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        mFetchAvailableTracksUseCase.unregisterListener(this)
        mDownloadTrackUseCase.unregisterListener(this)
        mFetchDownloadedTracks.unregisterListener(this)
    }

    override fun onAvailableTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }

    override fun onAvailableTracksFetchFailed() {
        Timber.e("Available tracks fetch failed")
    }

    override fun onDownloadTrackStarted(track: Track) {
        Timber.i("Download of track ${track.videoKey} STARTED")
    }

    override fun onDownloadTrackFinished(track: Track, path: String) {
        Timber.i("Download of track ${track.videoKey} FINISHED")
        val downloadDirectory = File(path)
        Timber.d("List of files in path $path:")
        downloadDirectory.listFiles()?.forEach {
            if (it.isDirectory) {
                Timber.d("Dir: ${it.absolutePath}")
            } else if (it.isFile) {
                Timber.d("File: ${it?.absoluteFile}, size: ${it.length() / 1000}KB (${it.length() / 1000000}MB)")
            }
        }
        viewMvc.navigateToPlayer(track)
    }

    override fun onDownloadTrackError() {
        Timber.i("Download of track FAILED")
    }

    override fun onTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }
}