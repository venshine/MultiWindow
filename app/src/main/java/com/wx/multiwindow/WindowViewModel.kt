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
 * 窗口状态快照：尺寸取自 Configuration.screenWidthDp / screenHeightDp，随窗口大小变化
 *
 * @author venshine
 */
data class WindowState(
    val widthDp: Int,
    val heightDp: Int,
    val inMultiWindow: Boolean,
    val inPip: Boolean,
)

/**
 * 状态页的回调日志：Activity 因主题切换等原因重建后日志仍在
 *
 * @author venshine
 */
class WindowViewModel : ViewModel() {

    private val _log = ArrayList<String>()

    /** 日志只读视图，按发生顺序排列（最新在末尾） */
    val log: List<String> get() = _log

    fun addLog(line: String) {
        _log.add(line)
        // 只保留最近 60 条，避免长时间拖动分隔条后无限增长
        if (_log.size > MAX_LINES) _log.removeAt(0)
    }

    fun clearLog() {
        _log.clear()
    }

    private companion object {
        const val MAX_LINES = 60
    }
}
