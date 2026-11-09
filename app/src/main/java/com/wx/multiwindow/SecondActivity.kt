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

/**
 * 第二页：没有声明 configChanges，窗口尺寸变化时会被系统重建，靠 ViewModel 保留日志。
 *
 * @author venshine
 */
class SecondActivity : BaseActivity() {

    override val pageTitle = R.string.second_title
    override val targetClass = FirstActivity::class.java
    override val dragPayload = R.string.drag_payload_second
}
