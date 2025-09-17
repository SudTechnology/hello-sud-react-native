package com.rtnsudgip.sudgame;

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.util.Arrays
import java.util.Collections


class SudMGPPlugin(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val reactContext: ReactApplicationContext = reactContext

    companion object {
        fun getReactPackage(): ReactPackage {
            return object : ReactPackage {
                override fun createNativeModules(reactContext: ReactApplicationContext): List<com.facebook.react.bridge.NativeModule> {
                    val sudGipPlugin = SudMGPPlugin(reactContext)
                    SudGameManager.shared.setSudMgpPlugin(sudGipPlugin)
                    return Arrays.asList(sudGipPlugin)
                }

                override fun createViewManagers(reactContext: ReactApplicationContext): List<com.facebook.react.uimanager.ViewManager<*, *>> {
                    return listOf(SudMGPPlugin.ViewManager())
                }
            }
        }
    }



    override fun getName(): String {
        return "SudMGPPlugin"
    }

    @ReactMethod
    fun configGameRect(param: String, promise: Promise) {
        // 实现加载游戏的逻辑
        println("configGameRect : $param")
        SudGameManager.shared.configGameRect(param)
        promise.resolve("Game loaded successfully")
    }

    @ReactMethod
    fun configGameCfg(param: String, promise: Promise) {
        // 实现加载游戏的逻辑
        println("configGameCfg : $param")
        SudGameManager.shared.configGameCfg(param)
        promise.resolve("Game loaded successfully")
    }

    @ReactMethod
    fun loadGame(param: String, promise: Promise) {
        // 实现加载游戏的逻辑
        println("loadGame: $param")
        SudGameManager.shared.loadGame(param)
        promise.resolve("Game loaded successfully")
    }

    @ReactMethod
    fun destroyGame(promise: Promise) {
        // 实现销毁游戏的逻辑
        println("destroyGame")
        SudGameManager.shared.destroyGame()
        promise.resolve("Game destroyGame successfully")
    }

    @ReactMethod
    fun updateCode(param: String,promise: Promise) {
        // 实现销毁游戏的逻辑
        println("updateCode:$param")
        SudGameManager.shared.updateCode(param)
        promise.resolve("Game updateCode successfully")
    }

    @ReactMethod
    fun notifyStateChange(param: String,promise: Promise) {
        // 实现销毁游戏的逻辑
        println("notifyAppStateChanged:$param")
        SudGameManager.shared.notifyStateChange(param)
        promise.resolve("Game notifyAppStateChanged successfully")
    }

    fun notifyReactOnGameStateChanged(state: String, dataJson:String) {
        val params: WritableMap = Arguments.createMap()
        params.putString("state", state)
        params.putString("dataJson", dataJson)
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onGameStateChanged", params)
    }

    fun notifyReactOnGetCode(appId:String, userId :String) {
        val params: WritableMap = Arguments.createMap()
        params.putString("appId", appId)
        params.putString("userId", userId)
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onGetCode", params)
    }

    class ViewManager : com.facebook.react.uimanager.SimpleViewManager<View>() {
        companion object {
            const val REACT_CLASS = "SudMGPPluginView"
        }

        override fun getName(): String {
            return REACT_CLASS
        }

        override fun createViewInstance(reactContext: com.facebook.react.uimanager.ThemedReactContext): View {
            val gameView = SudGameManager.shared.getGameView(reactContext)
            return gameView
        }
    }

    fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager> {
        return Collections.singletonList(ViewManager())
    }



}
