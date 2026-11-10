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

import android.content.pm.ActivityInfo
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** 配置变化后仍然保留的状态：计数和创建次数 */
class ConfigViewModel : ViewModel() {
    var counter = 0

    /** Activity 被创建的次数，大于 1 说明被重建过 */
    var created = 0
}

/**
 * 演示五：配置变化保留状态。本页不声明 configChanges，窗口尺寸变化、主题切换、点「立即重建」
 * 都会销毁并重建 Activity：普通字段归零，ViewModel 里的值保留。
 *
 * @author venshine
 */
class ConfigActivity : BaseActivity(R.layout.activity_config) {

    /** 普通字段：随 Activity 一起销毁 */
    private var plain = 0
    private lateinit var viewModel: ConfigViewModel

    override fun onBind() {
        setToolbar(R.string.demo_config, "ViewModel")
        viewModel = ViewModelProvider(this)[ConfigViewModel::class.java]
        viewModel.created++
        findViewById<TextView>(R.id.config_plus).setOnClickListener {
            plain++
            viewModel.counter++
            render()
        }
        findViewById<TextView>(R.id.config_recreate).setOnClickListener { recreate() }
        findViewById<TextView>(R.id.config_rotate).setOnClickListener {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        render()
    }

    override fun onWindowEvent(event: String, state: WindowState) = bindWindowState(state)

    private fun render() {
        findViewById<TextView>(R.id.config_plain_value).text = plain.toString()
        findViewById<TextView>(R.id.config_vm_value).text = viewModel.counter.toString()
        findViewById<TextView>(R.id.config_created).text = getString(R.string.config_created, viewModel.created)
        findViewById<TextView>(R.id.config_instance).text =
            getString(R.string.config_instance, Integer.toHexString(System.identityHashCode(this)))
    }
}
