package com.sal3awy.thed

import android.content.Context
import androidx.work.*
import com.sal3awy.thed.dagger.AppComponent
import com.sal3awy.thed.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.util.concurrent.TimeUnit


class App : DaggerApplication() {

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder().application(this).build()
        return appComponent!!
    }

    companion object {
        fun get(activity: Context) = activity.applicationContext as App
    }

    fun appComponent () = appComponent
}