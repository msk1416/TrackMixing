package com.smascaro.trackmixing.common.di.main

import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.details.view.TrackDetailsViewMvc
import com.smascaro.trackmixing.details.view.TrackDetailsViewMvcImpl
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvcImpl
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class MainModule {
//    @MainScope
//    @Provides
//    @RetrofitForJsonData
//    fun provideRetrofitInstanceWithJson(): Retrofit {
//        return Retrofit.Builder().apply {
//            baseUrl(NODE_BASE_URL)
//            addConverterFactory(GsonConverterFactory.create())
//        }.build()
//    }
//
//    @MainScope
//    @Provides
//    fun provideNodeApi(@RetrofitForJsonData retrofit: Retrofit): NodeApi {
//        return retrofit.create(NodeApi::class.java)
//    }
//
//    @MainScope
//    @Provides
//    @RetrofitForBinaryData
//    fun provideRetrofitInstanceForBinaryData(): Retrofit {
//        return Retrofit.Builder().apply {
//            baseUrl(NODE_BASE_URL)
//        }.build()
//    }
//
//    @MainScope
//    @Provides
//    fun provideNodeApiForBinaryDownloads(@RetrofitForBinaryData retrofit: Retrofit): NodeDownloadsApi {
//        return retrofit.create(NodeDownloadsApi::class.java)
//    }

    @MainScope
    @Provides
    fun provideNavController(baseActivity: BaseActivity): NavController {
        return baseActivity.findNavController(R.id.nav_host_fragment)
    }

    @Module
    interface ViewMvcBindings {
        @MainScope
        @Binds
        fun provideTracksListViewMvc(tracksListViewMvcImpl: TracksListViewMvcImpl): TracksListViewMvc

        @MainScope
        @Binds
        fun provideTrackDetailsViewMvcImpl(trackDetailsViewMvcImpl: TrackDetailsViewMvcImpl): TrackDetailsViewMvc

        @MainScope
        @Binds
        fun provideBottomPlayerViewMvc(bottomPlayerViewMvcImpl: BottomPlayerViewMvcImpl): BottomPlayerViewMvc

        @MainScope
        @Binds
        fun provideBottomProgressViewMvc(bottomProgressViewMvcImpl: BottomProgressViewMvcImpl): BottomProgressViewMvc
    }
}
