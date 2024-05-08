package com.softcat.stockviewer

import android.app.Application
import com.softcat.stockviewer.di.utilsModule
import com.softcat.stockviewer.di.viewModelModule
import org.koin.core.context.GlobalContext.startKoin

class StockViewerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(viewModelModule, utilsModule)
        }
    }
}