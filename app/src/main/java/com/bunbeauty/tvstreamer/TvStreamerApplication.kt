package com.bunbeauty.tvstreamer

import android.app.Application
import com.bunbeauty.tvstreamer.data.di.dataModule
import com.bunbeauty.tvstreamer.data.di.dataSourceModule
import com.bunbeauty.tvstreamer.domain.di.domainModule
import com.bunbeauty.tvstreamer.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

class TvStreamerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@TvStreamerApplication)
        }
    }
}


fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dataModule(),
        domainModule(),
        presentationModule(),
        dataSourceModule()
    )
}
