package com.smascaro.trackmixing.common.di

import android.content.Context
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.di.player.PlayerComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewMvcBuildersModule::class, AppSubcomponents::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun playerComponent(): PlayerComponent.Factory
}