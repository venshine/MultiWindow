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

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

/**
 * 两个演示页的公共基类：实时显示窗口尺寸 / 多窗口 / 画中画状态，打印生命周期，
 * 支持在相邻窗口打开另一个页面，以及跨窗口拖放文本。
 *
 * @author venshine
 */
abstract class BaseActivity : AppCompatActivity() {

    /** 本页标题 */
    protected abstract val pageTitle: Int

    /** "在相邻窗口打开" 要启动的目标页 */
    protected abstract val targetClass: Class<out BaseActivity>

    /** 拖拽源文本 */
    protected abstract val dragPayload: Int

    protected lateinit var viewModel: WindowViewModel
    private lateinit var infoView: TextView
    private lateinit var logView: TextView
    private lateinit var dropView: TextView

    // 多窗口 / 画中画标志由回调写入；回调之外用 Activity 自身的查询兜底
    private var inMultiWindow = false
    private var inPip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window)
        setTitle(pageTitle)
        viewModel = ViewModelProvider(this)[WindowViewModel::class.java]

        infoView = findViewById(R.id.window_info)
        logView = findViewById(R.id.log)
        dropView = findViewById(R.id.drop_zone)

        // targetSdk 35+ 强制 edge-to-edge，给根布局补上左右和底部的系统栏边距
        val root = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, bars.bottom)
            insets
        }

        findViewById<Button>(R.id.open_adjacent).setOnClickListener { openAdjacent() }
        findViewById<Button>(R.id.clear_log).setOnClickListener {
            viewModel.clearLog()
            renderLog()
        }
        setupPip(findViewById(R.id.enter_pip))
        setupDrag(findViewById(R.id.drag_source))
        setupDrop(dropView)

        inMultiWindow = isInMultiWindowMode
        inPip = isInPictureInPictureMode
        refresh()
        log("onCreate")
    }

    /** 子类按需把 PiP 按钮接上；默认隐藏 */
    protected open fun setupPip(button: Button) {
        button.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        // API 24/25 上没有带 Configuration 的回调，这里用查询兜底
        inMultiWindow = isInMultiWindowMode
        inPip = isInPictureInPictureMode
        refresh()
        log("onResume")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        // 重建时 ViewModel 还在，销毁事件也记下来；此时 TextView 即将销毁，不再渲染
        viewModel.addLog("onDestroy")
    }

    /** 用带 Configuration 的重载（API 26+）；旧的单参数重载已在 API 26 废弃 */
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        inMultiWindow = isInMultiWindowMode
        refresh(newConfig)
        log("onMultiWindowModeChanged($isInMultiWindowMode)")
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPip = isInPictureInPictureMode
        refresh(newConfig)
        log("onPictureInPictureModeChanged($isInPictureInPictureMode)")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refresh(newConfig)
        log("onConfigurationChanged")
    }

    /** 重新读取窗口状态并更新界面 */
    protected fun refresh(config: Configuration = resources.configuration) {
        val state = WindowState(config.screenWidthDp, config.screenHeightDp, inMultiWindow, inPip)
        viewModel.state = state
        infoView.text = formatState(this, state)
    }

    private fun log(line: String) {
        viewModel.addLog(line)
        renderLog()
    }

    private fun renderLog() {
        logView.text = viewModel.log.joinToString("\n")
    }

    /** 在相邻窗口启动目标页；系统会尽力而为，不保证成功 */
    private fun openAdjacent() {
        val intent = Intent(this, targetClass)
            .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setupDrag(source: TextView) {
        source.setText(dragPayload)
        source.setOnLongClickListener { v ->
            val clip = ClipData.newPlainText("text", (v as TextView).text)
            // DRAG_FLAG_GLOBAL 才允许拖到别的窗口 / 别的 App
            v.startDragAndDrop(clip, View.DragShadowBuilder(v), null, View.DRAG_FLAG_GLOBAL)
        }
    }

    private fun setupDrop(zone: TextView) {
        zone.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> DropParser.accepts(event.clipDescription)
                DragEvent.ACTION_DROP -> {
                    val text = DropParser.parseText(this, event.clipData)
                    if (text != null) {
                        zone.text = getString(R.string.dropped, text)
                        log("ACTION_DROP: $text")
                    }
                    text != null
                }
                else -> true
            }
        }
    }

    companion object {
        /** 状态文案格式化；单独抽出以便测试断言 */
        fun formatState(context: Context, s: WindowState): String = context.getString(
            R.string.window_info,
            s.widthDp,
            s.heightDp,
            context.getString(if (s.inMultiWindow) R.string.yes else R.string.no),
            context.getString(if (s.inPip) R.string.yes else R.string.no),
        )
    }
}
