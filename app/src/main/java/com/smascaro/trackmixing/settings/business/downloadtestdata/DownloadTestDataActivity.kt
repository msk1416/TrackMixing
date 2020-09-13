package com.smascaro.trackmixing.settings.business.downloadtestdata

import android.os.Bundle
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.di.settings.SettingsComponent
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import javax.inject.Inject

class DownloadTestDataActivity : BaseActivity() {
    lateinit var settingsComponent: SettingsComponent
    @Inject lateinit var navigationHelper: NavigationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        settingsComponent =
            (application as TrackMixingApplication).appComponent.settingsComponent().create()
        settingsComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_test_data)
        title = "Confirm download"
        navigationHelper.bindNavController(findNavController(R.id.nav_host_fragment_download_test_data))
    }

    override fun onBackPressed() {
        if (!navigationHelper.backAndPop()) {
            super.onBackPressed()
        }
    }
}