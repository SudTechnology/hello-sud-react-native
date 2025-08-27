package com.rtnsudgip

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.RTNSudGIPManagerInterface
import com.facebook.react.viewmanagers.RTNSudGIPManagerDelegate

@ReactModule(name = SudGIPManager.NAME)
class SudGIPManager(context: ReactApplicationContext) : SimpleViewManager<SudGIP>(), RTNSudGIPManagerInterface<SudGIP> {
  private val delegate: RTNSudGIPManagerDelegate<SudGIP,SudGIPManager> = RTNSudGIPManagerDelegate<SudGIP,SudGIPManager>(this)

  override fun getDelegate(): ViewManagerDelegate<SudGIP> = delegate

  override fun getName(): String = NAME

  override fun createViewInstance(context: ThemedReactContext): SudGIP = SudGIP(context)

  @ReactProp(name = "text")
  override fun setText(view: SudGIP, text: String?) {
    view.text = text
  }

  companion object {
    const val NAME = "RTNSudGIP"
  }
}
