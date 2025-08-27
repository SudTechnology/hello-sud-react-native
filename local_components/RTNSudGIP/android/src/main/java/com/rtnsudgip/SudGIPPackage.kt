package com.rtnsudgip

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.rtnsudgip.sudgame.SudMGPPlugin
import com.rtnsudgip.sudgame.SudGameManager

class SudGIPPackage : ReactPackage {
  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
    listOf(SudGIPManager(reactContext), SudMGPPlugin.ViewManager())

  //override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
   // emptyList()
                   override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
                       val sudGipPlugin = SudMGPPlugin(reactContext)
                       SudGameManager.shared.setSudMgpPlugin(sudGipPlugin)
                       return listOf(sudGipPlugin)
                       //return Arrays.asList(sudGipPlugin)
                   }
}
