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
import android.view.DragEvent
import android.view.View
import android.widget.TextView

/**
 * 演示三：跨窗口拖放文本。长按小标签拖到投放区；点「在相邻窗口再开一份」后可在两个窗口之间拖。
 * 拖拽源用 DRAG_FLAG_GLOBAL，才允许拖出本窗口；接收方只认 text/plain（见 [DropParser]）。
 *
 * @author venshine
 */
class DragActivity : BaseActivity(R.layout.activity_drag) {

    private lateinit var zone: TextView
    private lateinit var received: TextView
    private val items = ArrayList<String>()

    override fun onBind() {
        setToolbar(R.string.demo_drag, "startDragAndDrop")
        findViewById<TextView>(R.id.window_label).text = getString(R.string.drag_window, taskId)
        findViewById<View>(R.id.open_copy).setOnClickListener {
            startActivity(adjacentIntent(DragActivity::class.java, newInstance = true))
        }
        for (id in intArrayOf(R.id.drag_a, R.id.drag_b, R.id.drag_c)) setupSource(findViewById(id))
        zone = findViewById(R.id.drop_zone)
        received = findViewById(R.id.drop_log)
        setupDrop(zone)
        render()
    }

    private fun setupSource(chip: TextView) {
        chip.setOnLongClickListener { v ->
            // 带上窗口编号，一眼看出文本来自哪个窗口
            val text = "${(v as TextView).text} @#$taskId"
            val clip = ClipData.newPlainText("text", text)
            // DRAG_FLAG_GLOBAL 才允许拖到别的窗口 / 别的 App
            v.startDragAndDrop(clip, View.DragShadowBuilder(v), null, View.DRAG_FLAG_GLOBAL)
        }
    }

    private fun setupDrop(target: TextView) {
        target.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> DropParser.accepts(event.clipDescription)
                DragEvent.ACTION_DRAG_ENTERED -> {
                    target.isSelected = true
                    true
                }
                DragEvent.ACTION_DRAG_EXITED, DragEvent.ACTION_DRAG_ENDED -> {
                    target.isSelected = false
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val text = DropParser.parseText(this, event.clipData)
                    if (text != null) onDropped(text)
                    text != null
                }
                else -> true
            }
        }
    }

    /** 收到一条文本：记入列表（最新在最上面）并刷新计数 */
    internal fun onDropped(text: String) {
        items.add(0, text)
        render()
    }

    private fun render() {
        findViewById<TextView>(R.id.drop_count).text = getString(R.string.drag_received_count, items.size)
        received.text = items.joinToString("\n") { "← $it" }
        zone.text = items.firstOrNull()?.let { getString(R.string.dropped, it) } ?: getString(R.string.drop_hint)
    }
}
