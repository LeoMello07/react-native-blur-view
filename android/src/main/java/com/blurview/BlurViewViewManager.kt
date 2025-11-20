package com.blurview

import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class BlurViewViewManager : ViewGroupManager<BlurView>() {

    override fun getName() = "BlurView"

    override fun createViewInstance(reactContext: ThemedReactContext): BlurView {
        return BlurView(reactContext)
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
        return mutableMapOf()
    }

    @ReactProp(name = "blurIntensity", defaultDouble = 10.0)
    fun setBlurIntensity(view: BlurView, value: Double) {
        view.setBlurIntensity(value)
    }

    @ReactProp(name = "saturationIntensity", defaultFloat = 1f)
    fun setSaturationIntensity(view: BlurView, value: Float) {
        view.setSaturationIntensity(value)
    }

    @ReactProp(name = "fadePercent", defaultFloat = 0f)
    fun setFadePercent(view: BlurView, value: Float) {
        view.setFadePercent(value)
    }

    @ReactProp(name = "fadeStyle")
    fun setFadeStyle(view: BlurView, value: String?) {
        view.setFadeStyle(value)
    }

    @ReactProp(name = "blurStyle")
    fun setBlurStyle(view: BlurView, value: String?) {
        view.setBlurStyle(value)
    }
}
