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

import android.content.pm.PackageManager
import android.view.View
import android.widget.Button

/**
 * 第一页：可以进入画中画（需要在 Manifest 声明 supportsPictureInPicture 并自行处理配置变化）。
 *
 * @author venshine
 */
class FirstActivity : BaseActivity() {

    override val pageTitle = R.string.first_title
    override val targetClass = SecondActivity::class.java
    override val dragPayload = R.string.drag_payload_first

    override fun setupPip(button: Button) {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            button.visibility = View.GONE
            return
        }
        button.setOnClickListener { enterPictureInPictureMode() }
    }
}
