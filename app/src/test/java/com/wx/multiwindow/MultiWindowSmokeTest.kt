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
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import java.time.Duration

/**
 * 示例页冒烟测试：每个页面在浅色、深色下都能启动，关键交互的结果符合预期
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class MultiWindowSmokeTest {

    private val created = ArrayList<org.robolectric.android.controller.ActivityController<*>>()

    @After
    fun tearDown() {
        // 销毁本用例启动过的 Activity，并把深浅色恢复成浅色
        created.forEach { runCatching { it.destroy() } }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun idle() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
    }

    private fun <T : Activity> launch(type: Class<T>): T {
        val controller = Robolectric.buildActivity(type).setup()
        created.add(controller)
        idle()
        return controller.get()
    }

    private fun configOf(w: Int, h: Int) =
        Configuration().apply { screenWidthDp = w; screenHeightDp = h }

    private fun Activity.text(id: Int) = findViewById<TextView>(id).text.toString()

    private val screens = listOf(
        MainActivity::class.java, StatusActivity::class.java, AdjacentActivity::class.java,
        PeerActivity::class.java, DragActivity::class.java, PipActivity::class.java, ConfigActivity::class.java,
    )

    @Test
    fun allScreensLaunchInLightMode() {
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为浅色", !DemoTheme.isNight(activity))
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(activity))
        }
    }

    @Test
    fun homeShowsVersionAndOpensDemos() {
        val home = launch(MainActivity::class.java)
        assertTrue(home.text(R.id.version_chip).startsWith("v"))
        home.findViewById<View>(R.id.card_pip).performClick()
        assertEquals(PipActivity::class.java.name, shadowOf(home).nextStartedActivity.component?.className)
        home.findViewById<View>(R.id.card_status).performClick()
        assertEquals(StatusActivity::class.java.name, shadowOf(home).nextStartedActivity.component?.className)
    }

    @Test
    fun homePreviewFollowsWindowCallbacks() {
        val home = launch(MainActivity::class.java)
        home.onMultiWindowModeChanged(true, configOf(320, 480))
        assertEquals(home.getString(R.string.state_size_value, 320, 480), home.text(R.id.state_size))
        assertTrue(home.findViewById<View>(R.id.cell_multi).isSelected)
        home.onPictureInPictureModeChanged(true, configOf(160, 90))
        assertTrue(home.findViewById<View>(R.id.cell_pip).isSelected)
    }

    @Test
    fun statusLogRecordsCallbacksAndClears() {
        val status = launch(StatusActivity::class.java)
        status.onMultiWindowModeChanged(true, configOf(300, 300))
        assertTrue(status.text(R.id.log).contains("onMultiWindowModeChanged(true)"))
        assertTrue(status.text(R.id.state_multi) == status.getString(R.string.yes))
        status.findViewById<View>(R.id.clear_log).performClick()
        assertEquals("", status.text(R.id.log))
    }

    @Test
    fun adjacentButtonStartsPeerWithAdjacentFlag() {
        val page = launch(AdjacentActivity::class.java)
        page.findViewById<View>(R.id.open_adjacent).performClick()
        val intent = shadowOf(page).nextStartedActivity
        assertEquals(PeerActivity::class.java.name, intent.component?.className)
        assertTrue(intent.flags and android.content.Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT != 0)
    }

    @Test
    fun dragOpenCopyUsesMultipleTaskAndDropUpdatesZone() {
        val page = launch(DragActivity::class.java)
        page.findViewById<View>(R.id.open_copy).performClick()
        val intent = shadowOf(page).nextStartedActivity
        assertEquals(DragActivity::class.java.name, intent.component?.className)
        assertTrue(intent.flags and android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK != 0)

        page.onDropped("hello")
        assertEquals(page.getString(R.string.drag_received_count, 1), page.text(R.id.drop_count))
        assertTrue(page.text(R.id.drop_zone).contains("hello"))
        assertTrue(page.text(R.id.drop_log).contains("hello"))
    }

    @Test
    fun pipModeHidesControlsAndClockTicks() {
        val page = launch(PipActivity::class.java)
        val before = page.text(R.id.pip_clock)
        page.onPictureInPictureModeChanged(true, configOf(200, 112))
        assertEquals(View.GONE, page.findViewById<View>(R.id.pip_chrome).visibility)
        page.onPictureInPictureModeChanged(false, configOf(411, 891))
        assertEquals(View.VISIBLE, page.findViewById<View>(R.id.pip_chrome).visibility)
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(3))
        assertTrue("时钟应当走动", page.text(R.id.pip_clock) != before)
    }

    @Test
    fun viewModelValueSurvivesRecreateButPlainFieldDoesNot() {
        val controller = Robolectric.buildActivity(ConfigActivity::class.java).setup()
        created.add(controller)
        val first = controller.get()
        first.findViewById<View>(R.id.config_plus).performClick()
        first.findViewById<View>(R.id.config_plus).performClick()
        assertEquals("2", first.text(R.id.config_plain_value))
        assertEquals("2", first.text(R.id.config_vm_value))

        controller.recreate()
        val second = controller.get()
        assertEquals("0", second.text(R.id.config_plain_value))
        assertEquals("2", second.text(R.id.config_vm_value))
        assertEquals(second.getString(R.string.config_created, 2), second.text(R.id.config_created))
    }

    @Test
    fun dropParserReadsText() {
        val ctx = RuntimeEnvironment.getApplication()
        assertEquals("hello", DropParser.parseText(ctx, ClipData.newPlainText("l", "  hello ")))
        assertNull(DropParser.parseText(ctx, null))
        assertNull(DropParser.parseText(ctx, ClipData.newPlainText("l", "   ")))
        val html = ClipData("l", arrayOf(ClipDescription.MIMETYPE_TEXT_HTML), ClipData.Item("<b>x</b>"))
        assertNull(DropParser.parseText(ctx, html))
        assertTrue(DropParser.accepts(ClipData.newPlainText("l", "x").description))
    }
}
