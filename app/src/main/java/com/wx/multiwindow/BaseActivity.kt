/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.multiwindow

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 公共外观与窗口状态跟踪：
 * - 外观：Android 15+ 强制全面屏的边距、深浅色切换、返回、二级页顶栏
 * - 窗口：多窗口 / 画中画标志由回调写入，回调之外用 Activity 自身的查询兜底；
 *   任何变化都通过 [onWindowEvent] 通知子类
 *
 * @author venshine
 */
abstract class BaseActivity(@LayoutRes private val layout: Int) : AppCompatActivity() {

    private var inMultiWindow = false
    private var inPip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        inMultiWindow = isInMultiWindowMode
        inPip = isInPictureInPictureMode
        onBind()
        dispatch("onCreate")
    }

    /** 布局加载完成后绑定控件 */
    protected open fun onBind() {}

    /** 生命周期或窗口回调发生；[event] 是回调名（带参数值），[state] 是此刻的窗口状态 */
    protected open fun onWindowEvent(event: String, state: WindowState) {}

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // targetSdk 35 起，Android 15+ 强制全面屏，内容需要避开状态栏、导航栏和挖孔
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        val light = !DemoTheme.isNight(this)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = light
            isAppearanceLightNavigationBars = light
        }

        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.theme_toggle)?.let { button ->
            val night = DemoTheme.isNight(this)
            button.setImageResource(if (night) R.drawable.ic_sun else R.drawable.ic_moon)
            button.contentDescription = getString(if (night) R.string.theme_to_light else R.string.theme_to_dark)
            button.setOnClickListener { DemoTheme.toggle(this) }
        }
    }

    /** 设置二级页顶栏的标题和右侧 API 标签 */
    protected fun setToolbar(@StringRes title: Int, chip: String) {
        findViewById<TextView>(R.id.toolbar_title).setText(title)
        findViewById<TextView>(R.id.toolbar_chip).apply {
            text = chip
            visibility = View.VISIBLE
        }
    }

    /** 把窗口状态写进 include_window_state 布局里的三个格子 */
    protected fun bindWindowState(state: WindowState) {
        findViewById<TextView>(R.id.state_size).text =
            getString(R.string.state_size_value, state.widthDp, state.heightDp)
        bindFlag(R.id.state_multi, R.id.cell_multi, state.inMultiWindow)
        bindFlag(R.id.state_pip, R.id.cell_pip, state.inPip)
    }

    private fun bindFlag(textId: Int, cellId: Int, on: Boolean) {
        findViewById<TextView>(textId).setText(if (on) R.string.yes else R.string.no)
        findViewById<View>(cellId).isSelected = on
    }

    override fun onStart() {
        super.onStart()
        dispatch("onStart")
    }

    override fun onResume() {
        super.onResume()
        // API 24/25 上没有带 Configuration 的回调，这里用查询兜底
        inMultiWindow = isInMultiWindowMode
        inPip = isInPictureInPictureMode
        dispatch("onResume")
    }

    override fun onPause() {
        super.onPause()
        dispatch("onPause")
    }

    override fun onStop() {
        super.onStop()
        dispatch("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        dispatch("onDestroy")
    }

    /** 用带 Configuration 的重载（API 26+）；旧的单参数重载已在 API 26 废弃 */
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        inMultiWindow = isInMultiWindowMode
        dispatch("onMultiWindowModeChanged($isInMultiWindowMode)", newConfig)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPip = isInPictureInPictureMode
        dispatch("onPictureInPictureModeChanged($isInPictureInPictureMode)", newConfig)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dispatch("onConfigurationChanged", newConfig)
    }

    private fun dispatch(event: String, config: Configuration = resources.configuration) {
        onWindowEvent(event, WindowState(config.screenWidthDp, config.screenHeightDp, inMultiWindow, inPip))
    }
}
