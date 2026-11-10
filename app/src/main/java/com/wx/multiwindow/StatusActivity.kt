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

import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 演示一：窗口状态与回调日志。拖动分屏分隔条、旋转、进入画中画，状态和日志实时变化。
 * 在 Manifest 里声明了 configChanges，尺寸变化由本页自己处理（不重建），所以日志能连续记录。
 *
 * @author venshine
 */
class StatusActivity : BaseActivity(R.layout.activity_status) {

    private lateinit var viewModel: WindowViewModel
    private lateinit var logView: TextView
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    override fun onBind() {
        setToolbar(R.string.demo_status, "Callbacks")
        viewModel = ViewModelProvider(this)[WindowViewModel::class.java]
        logView = findViewById(R.id.log)
        findViewById<TextView>(R.id.clear_log).setOnClickListener {
            viewModel.clearLog()
            renderLog()
        }
    }

    override fun onWindowEvent(event: String, state: WindowState) {
        bindWindowState(state)
        viewModel.addLog("${timeFormat.format(Date())}  $event")
        renderLog()
    }

    /** 最新的一条放在最上面，录屏时不用滚动 */
    private fun renderLog() {
        logView.text = viewModel.log.asReversed().joinToString("\n")
    }
}
