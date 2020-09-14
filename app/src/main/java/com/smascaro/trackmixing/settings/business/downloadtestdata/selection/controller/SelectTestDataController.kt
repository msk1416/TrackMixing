package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDao
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    private val downloadTrackUseCase: DownloadTrackUseCase,
    private val downloadsDao: DownloadsDao,
    p_navigationHelper: NavigationHelper
) :
    BaseNavigatorController<SelectTestDataViewMvc>(p_navigationHelper),
    SelectTestDataViewMvc.Listener {
    private var totalDownloadBytes = 0
    private var tracksToDownload = mutableListOf<TestDataBundleInfo>()
    fun onStart() {
        viewMvc.registerListener(this)
        downloadTestDataUseCase.getTestDataBundleInfo {
            when (it) {
                is DownloadTestDataUseCase.Result.Success -> {
                    viewMvc.bindTracks(it.tracks)
                    checkAlreadyDownloadedItems(it.tracks)
                }
                is DownloadTestDataUseCase.Result.Failure -> viewMvc.showError(it.throwable.localizedMessage)
            }
        }
    }

    private fun checkAlreadyDownloadedItems(tracks: List<TestDataBundleInfo>) {
        CoroutineScope(Dispatchers.IO).launch {

            val downloads = downloadsDao.getAll()
            val downloadedTestData = tracks.filter { testDataItem ->
                downloads.contains {
                    it.sourceVideoKey == testDataItem.videoKey
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                viewMvc.bindAlreadyDownloadedData(downloadedTestData)
            }
        }
    }

    inline fun <T> Iterable<T>.contains(predicate: (elem: T) -> Boolean): Boolean {
        return this.any { predicate(it) }
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    override fun onItemSelected(item: TestDataBundleInfo) {
        tracksToDownload.add(item)
        viewMvc.updateSizeToDownload(getTotalSizeToDownload())
    }

    override fun onItemUnselected(item: TestDataBundleInfo) {
        tracksToDownload.remove(item)
        viewMvc.updateSizeToDownload(getTotalSizeToDownload())
    }

    override fun onDownloadButtonClicked() {
        navigationHelper.toTestDataDownload(tracksToDownload)
    }

    private fun getTotalSizeToDownload(): Int {
        return tracksToDownload.sumBy { it.size }
    }
}