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

import android.content.Intent
import android.widget.TextView

/**
 * 演示二：用 FLAG_ACTIVITY_LAUNCH_ADJACENT 把另一页开在相邻窗口。
 * 系统只是尽力而为：全屏手机上会直接全屏打开，需要先进入分屏、自由窗口或在大屏上。
 *
 * @author venshine
 */
class AdjacentActivity : BaseActivity(R.layout.activity_adjacent) {

    override fun onBind() {
        setToolbar(R.string.demo_adjacent, "LAUNCH_ADJACENT")
        findViewById<TextView>(R.id.open_adjacent).setOnClickListener { openPeer() }
    }

    override fun onWindowEvent(event: String, state: WindowState) = bindWindowState(state)

    private fun openPeer() {
        startActivity(adjacentIntent(PeerActivity::class.java))
    }
}

/**
 * 在相邻窗口启动的 Intent；[newInstance] 为 true 时即使该页已有任务也再开一份
 * （跨窗口拖放需要同一页开两份）
 */
fun BaseActivity.adjacentIntent(target: Class<out BaseActivity>, newInstance: Boolean = false): Intent {
    var flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK
    if (newInstance) flags = flags or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    return Intent(this, target).addFlags(flags)
}
