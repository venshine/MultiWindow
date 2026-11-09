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

import androidx.lifecycle.ViewModel

/**
 * 当前窗口状态快照。
 *
 * @param widthDp 窗口宽度（dp），来自 Configuration.screenWidthDp
 * @param heightDp 窗口高度（dp），来自 Configuration.screenHeightDp
 */
data class WindowState(
    val widthDp: Int,
    val heightDp: Int,
    val inMultiWindow: Boolean,
    val inPip: Boolean,
)

/**
 * 保存日志和窗口状态。ViewModel 在配置变化（旋转、拖动分屏分割线）重建 Activity 后依然存在，
 * 所以日志不会丢。
 */
class WindowViewModel : ViewModel() {

    private val _log = ArrayList<String>()

    /** 日志只读视图 */
    val log: List<String> get() = _log

    /** 最近一次状态；Activity 每次刷新时写入 */
    var state: WindowState? = null

    fun addLog(line: String) {
        _log.add(line)
    }

    fun clearLog() {
        _log.clear()
    }
}
