package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.ui.asGB
import com.smascaro.trackmixing.common.utils.ui.asKB
import com.smascaro.trackmixing.common.utils.ui.asMB
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class SelectTestDataViewMvcImpl @Inject constructor(
    private val testDataAdapter: TestDataListAdapter,
    private val resourcesWrapper: ResourcesWrapper
) :
    BaseObservableViewMvc<SelectTestDataViewMvc.Listener>(),
    SelectTestDataViewMvc, TestDataListAdapter.Listener {
    private lateinit var totalDownloadSizeText: MaterialTextView
    private lateinit var recyclerViewTestDataBundleInfo: RecyclerView
    private lateinit var startDownloadButton: MaterialButton
    private lateinit var availableSpaceTextView: MaterialTextView
    private var availableBytes = Long.MAX_VALUE
    private var defaultMaterialTextColor: Int = 0

    override fun initialize() {
        super.initialize()
        recyclerViewTestDataBundleInfo = findViewById(R.id.rv_select_test_data_tracks)
        recyclerViewTestDataBundleInfo.layoutManager = LinearLayoutManager(getContext())
        (recyclerViewTestDataBundleInfo.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        recyclerViewTestDataBundleInfo.setHasFixedSize(true)
        testDataAdapter.setAdapterListener(this)
        recyclerViewTestDataBundleInfo.adapter = testDataAdapter

        startDownloadButton = findViewById(R.id.btn_select_test_data_start_download)
        totalDownloadSizeText = findViewById(R.id.tv_select_test_data_total_size)
        availableSpaceTextView = findViewById(R.id.tv_select_test_data_available_space)

        defaultMaterialTextColor = totalDownloadSizeText.textColors.defaultColor
        startDownloadButton.setOnClickListener {
            getListeners().forEach {
                it.onDownloadButtonClicked()
            }
        }

        availableSpaceTextView.text = ""
        updateTotalSize(0)
    }

    override fun bindTracks(tracks: List<TestDataBundleInfo>) {
        testDataAdapter.bindData(tracks)
    }

    override fun bindAvailableSpace(availableBytes: Long) {
        this.availableBytes = availableBytes
        var text = ""
        if (availableBytes > 1 * 1000 * 1000 * 1000) {
            text = availableBytes.asGB
        } else if (availableBytes > 1 * 1000 * 1000) {
            text = availableBytes.asMB
        } else if (availableBytes > 1 * 1000) {
            text = availableBytes.asKB
        } else {
            text = "$availableBytes bytes"
        }

        availableSpaceTextView.text =
            resourcesWrapper.getString(R.string.select_test_data_available_space, text)
    }

    override fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>) {
        testDataAdapter.bindAlreadyDownloadedData(downloadedTestData)
    }

    override fun enableDownloadButton() {
        startDownloadButton.isEnabled = true
    }

    override fun disableDownloadButton() {
        startDownloadButton.isEnabled = false
    }

    override fun updateSizeToDownload(bytesToDownload: Int) {
        updateTotalSize(bytesToDownload)
        if (bytesToDownload > availableBytes) {
            totalDownloadSizeText.setTextColor(Color.RED)
        } else {
            totalDownloadSizeText.setTextColor(defaultMaterialTextColor)
        }
    }

    override fun showError(message: String?) {
        Toast.makeText(getContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelectionChanged(
        item: TestDataBundleInfo,
        selected: Boolean
    ) {
        getListeners().forEach {
            if (selected) {
                it.onItemSelected(item)
            } else {
                it.onItemUnselected(item)
            }
        }
    }

    private fun updateTotalSize(bytes: Int) {
        totalDownloadSizeText.text = bytes.asMB
    }
}