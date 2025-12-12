package io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext

interface BaseLifecycle {

    fun onCreate()

    fun onStop(){}

    fun onDestroy(){}
}