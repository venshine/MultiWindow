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

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat

/**
 * 演示四：画中画。时钟和进度条持续走动，进入画中画后控制区隐藏，只剩走动的时钟。
 * 画中画要求在 Manifest 声明 supportsPictureInPicture 并自行处理 configChanges。
 *
 * @author venshine
 */
class PipActivity : BaseActivity(R.layout.activity_pip) {

    private val handler = Handler(Looper.getMainLooper())
    private var elapsedMs = 0L
    private lateinit var clock: TextView
    private lateinit var bar: ProgressBar
    private lateinit var auto: SwitchCompat

    private val ticker = object : Runnable {
        override fun run() {
            elapsedMs += TICK_MS
            renderClock()
            handler.postDelayed(this, TICK_MS)
        }
    }

    override fun onBind() {
        setToolbar(R.string.demo_pip, "PictureInPicture")
        clock = findViewById(R.id.pip_clock)
        bar = findViewById(R.id.pip_bar)
        auto = findViewById(R.id.pip_auto)
        val supported = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        findViewById<View>(R.id.pip_unsupported).visibility = if (supported) View.GONE else View.VISIBLE
        findViewById<View>(R.id.pip_enter).apply {
            visibility = if (supported) View.VISIBLE else View.GONE
            setOnClickListener { enterPip() }
        }
        // 自动进入（离开页面时）需要 Android 8.0+ 的参数
        auto.visibility = if (supported && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.VISIBLE else View.GONE
        auto.setOnCheckedChangeListener { _, _ -> updateParams() }
        updateParams()
        renderClock()
    }

    override fun onWindowEvent(event: String, state: WindowState) {
        bindWindowState(state)
        // 画中画里只留时钟
        findViewById<View>(R.id.pip_chrome).visibility = if (state.inPip) View.GONE else View.VISIBLE
        // 画中画窗口很小，缩小时钟并隐藏进度条，避免数字被裁切
        clock.textSize = if (state.inPip) 28f else 48f
        bar.visibility = if (state.inPip) View.GONE else View.VISIBLE
    }

    // 画中画在不同系统版本上 Activity 可能处于暂停态，所以按「可见」而不是「前台」走时
    override fun onStart() {
        super.onStart()
        handler.postDelayed(ticker, TICK_MS)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(ticker)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Android 12 起由 setAutoEnterEnabled 接管，8.0 - 11 在这里手动进入
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O until Build.VERSION_CODES.S &&
            auto.isChecked && !isInPictureInPictureMode) enterPip()
    }

    private fun renderClock() {
        val seconds = elapsedMs / 1000
        clock.text = String.format("%02d:%02d", seconds / 60 % 60, seconds % 60)
        bar.progress = (elapsedMs % 10_000 / 10).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun params(): PictureInPictureParams.Builder =
        PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))

    private fun updateParams() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val builder = params()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) builder.setAutoEnterEnabled(auto.isChecked)
        setPictureInPictureParams(builder.build())
    }

    @Suppress("DEPRECATION") // API 24/25 只有无参版本
    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) enterPictureInPictureMode(params().build())
        else enterPictureInPictureMode()
    }

    private companion object {
        const val TICK_MS = 100L
    }
}
