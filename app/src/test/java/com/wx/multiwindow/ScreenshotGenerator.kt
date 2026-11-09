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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * README 截图生成器：用 Robolectric 把首页渲染成 PNG。
 * 默认跳过；生成命令见 AGENTS.md（-Pscreenshots）。
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotGenerator {

    @Test
    @Config(qualifiers = "w411dp-h891dp-notnight-xxhdpi")
    fun light() = capture("light")

    @Test
    @Config(qualifiers = "w411dp-h891dp-night-xxhdpi")
    fun dark() = capture("dark")

    private fun capture(suffix: String) {
        assumeTrue(System.getProperty("screenshots") == "true")
        val activity = Robolectric.buildActivity(FirstActivity::class.java).setup().get()
        shadowOf(Looper.getMainLooper()).idle()
        val view = activity.window.decorView
        // Robolectric 不派发窗口 insets，内容区会被 ActionBar 盖住；手动让出 ActionBar 的高度
        val root = activity.findViewById<android.view.View>(R.id.root)
        root.setPadding(root.paddingLeft, activity.supportActionBar?.height ?: 0, root.paddingRight, root.paddingBottom)
        // 改过 padding 后按窗口尺寸重新测量布局
        view.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(view.width, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(view.height, android.view.View.MeasureSpec.EXACTLY),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        val dir = File(System.getProperty("screenshotDir") ?: "../screenshot").apply { mkdirs() }
        File(dir, "multi-window-$suffix.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        activity.finish()
    }
}
