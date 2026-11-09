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
import android.content.ClipData
import android.content.ClipDescription
import android.content.res.Configuration
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/** 冒烟测试：页面能启动、回调后文案更新、配置重建后日志不丢、拖放数据解析 */
@RunWith(RobolectricTestRunner::class)
class MultiWindowSmokeTest {

    private fun configOf(w: Int, h: Int) =
        Configuration().apply { screenWidthDp = w; screenHeightDp = h }

    private fun Activity.text(id: Int) = findViewById<TextView>(id).text.toString()

    @Test
    fun activitiesLaunch() {
        val first = Robolectric.buildActivity(FirstActivity::class.java).setup().get()
        val second = Robolectric.buildActivity(SecondActivity::class.java).setup().get()
        assertTrue(first.text(R.id.window_info).contains("dp"))
        assertTrue(second.text(R.id.log).contains("onCreate"))
    }

    @Test
    fun multiWindowCallbackUpdatesInfo() {
        val activity = Robolectric.buildActivity(SecondActivity::class.java).setup().get()
        activity.onMultiWindowModeChanged(true, configOf(320, 480))
        val expected = BaseActivity.formatState(activity, WindowState(320, 480, true, false))
        assertEquals(expected, activity.text(R.id.window_info))
        assertTrue(expected.contains("320 × 480"))

        activity.onPictureInPictureModeChanged(true, configOf(160, 90))
        assertEquals(
            BaseActivity.formatState(activity, WindowState(160, 90, true, true)),
            activity.text(R.id.window_info),
        )
        assertTrue(activity.text(R.id.log).contains("onMultiWindowModeChanged(true)"))
    }

    @Test
    fun logSurvivesRecreate() {
        val controller = Robolectric.buildActivity(SecondActivity::class.java).setup()
        controller.get().onMultiWindowModeChanged(true, configOf(300, 300))
        controller.recreate()
        val log = controller.get().text(R.id.log)
        assertTrue(log.contains("onMultiWindowModeChanged(true)"))
        assertTrue(log.contains("onDestroy"))
    }

    @Test
    fun dropParserReadsText() {
        val ctx = org.robolectric.RuntimeEnvironment.getApplication()
        assertEquals("hello", DropParser.parseText(ctx, ClipData.newPlainText("l", "  hello ")))
        assertNull(DropParser.parseText(ctx, null))
        assertNull(DropParser.parseText(ctx, ClipData.newPlainText("l", "   ")))
        val html = ClipData("l", arrayOf(ClipDescription.MIMETYPE_TEXT_HTML), ClipData.Item("<b>x</b>"))
        assertNull(DropParser.parseText(ctx, html))
        assertTrue(DropParser.accepts(ClipData.newPlainText("l", "x").description))
    }
}
