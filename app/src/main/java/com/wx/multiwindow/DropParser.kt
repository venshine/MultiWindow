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
import android.content.ClipDescription
import android.content.Context

/**
 * 拖放数据解析：只接受纯文本，其它类型一律忽略。
 *
 * 独立成纯函数，便于不依赖真实的 DragEvent 做单元测试。
 */
object DropParser {

    /** 拖拽源和接收方约定的 MIME 类型 */
    const val MIME_TEXT = ClipDescription.MIMETYPE_TEXT_PLAIN

    /** 该剪贴板数据是否可被接收（ACTION_DRAG_STARTED 阶段用） */
    fun accepts(description: ClipDescription?): Boolean =
        description?.hasMimeType(MIME_TEXT) == true

    /** 取出第一条非空文本，没有则返回 null */
    fun parseText(context: Context, clip: ClipData?): String? {
        if (clip == null || !accepts(clip.description)) return null
        for (i in 0 until clip.itemCount) {
            val text = clip.getItemAt(i).coerceToText(context)?.toString()?.trim()
            if (!text.isNullOrEmpty()) return text
        }
        return null
    }
}
