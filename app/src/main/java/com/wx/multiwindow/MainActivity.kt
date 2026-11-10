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

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

/**
 * 首页：当前窗口状态的即时预览 + 五个演示页入口
 *
 * @author venshine
 */
class MainActivity : BaseActivity(R.layout.activity_main) {

    /** 示例 App 与仓库同版本发布，版本号只在 app/build.gradle 的 versionName 维护一处 */
    private val version: String by lazy {
        packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
    }

    override fun onBind() {
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        findViewById<TextView>(R.id.version_chip).text = "v$version"
        findViewById<View>(R.id.card_status).setOnClickListener { open(StatusActivity::class.java) }
        bindCard(R.id.card_adjacent, R.drawable.ic_adjacent, R.string.demo_adjacent, R.string.demo_adjacent_desc,
            AdjacentActivity::class.java)
        bindCard(R.id.card_drag, R.drawable.ic_drag, R.string.demo_drag, R.string.demo_drag_desc,
            DragActivity::class.java)
        bindCard(R.id.card_pip, R.drawable.ic_pip, R.string.demo_pip, R.string.demo_pip_desc,
            PipActivity::class.java)
        bindCard(R.id.card_config, R.drawable.ic_refresh, R.string.demo_config, R.string.demo_config_desc,
            ConfigActivity::class.java)
    }

    override fun onWindowEvent(event: String, state: WindowState) = bindWindowState(state)

    private fun bindCard(@IdRes id: Int, @DrawableRes icon: Int, @StringRes title: Int, @StringRes desc: Int,
                         target: Class<out Activity>) {
        val card = findViewById<View>(id)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(icon)
        card.findViewById<TextView>(R.id.card_title).setText(title)
        card.findViewById<TextView>(R.id.card_desc).setText(desc)
        card.setOnClickListener { open(target) }
    }

    private fun open(target: Class<out Activity>) = startActivity(Intent(this, target))

    private fun openSource() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, SOURCE_URL, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val SOURCE_URL = "https://github.com/venshine/MultiWindow"
    }
}
