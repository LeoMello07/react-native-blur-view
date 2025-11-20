// android/src/main/java/com/blurview/BlurViewPackage.kt
package com.blurview

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class BlurViewPackage : ReactPackage {

    // Como não temos NativeModules, retornamos uma lista vazia
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return emptyList()
    }

    // Registra o ViewManager (ESSENCIAL)
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(BlurViewViewManager())
    }
}
