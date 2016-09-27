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
package com.wx.multiwindow;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wx.android.common.util.VibratorUtils;

/**
 * First Activity
 *
 * @author venshine
 */
public class Activity1 extends BaseActivity {

    private static StringBuilder mBuilder = new StringBuilder();
    private TextView mTextView;
    private ScrollView mScrollView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity1);
        setTitle("Frist Activity");
        mTextView = (TextView) findViewById(R.id.textview);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        mImageView = (ImageView) findViewById(R.id.imageview);
        mImageView.setTag("Drag an ImageView from First Activity");
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View view) {
                VibratorUtils.vibrate(mActivity, 100);
                Uri uri = getUri(mActivity, R.mipmap.ic_launcher);
                ClipData dragData = ClipData.newPlainText("", (CharSequence) view.getTag());
                ClipData.Item imageItem = new ClipData.Item(uri);
                dragData.addItem(imageItem);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                view.startDragAndDrop(dragData, shadow, view, View.DRAG_FLAG_GLOBAL);
                return true;
            }
        });
        print("onCreate", "#ff0000", false);
    }

    /**
     * 打印信息
     *
     * @param msg
     */
    public void print(String msg) {
        mBuilder.append(msg).append("<br />");
        mTextView.setText(Html.fromHtml(mBuilder.toString()));
    }

    /**
     * 打印富文本信息
     *
     * @param msg
     * @param color
     * @param bold
     */
    public void print(String msg, String color, boolean bold) {
        mBuilder.append(TextUtils.isEmpty(color) ? "" : "<font color='" + color + "'>").append(bold ? "<strong>" : "")
                .append(msg).append(bold ? "</strong>" : "").append(TextUtils.isEmpty(color) ? "" : "</font>")
                .append("<br />");
        mTextView.setText(Html.fromHtml(mBuilder.toString()));
    }

    /**
     * 跳转Activity2
     *
     * @param v
     */
    public void click(View v) {
        Intent intent = new Intent(this, Activity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 清理日志
     *
     * @param v
     */
    public void clear(View v) {
        mBuilder.delete(0, mBuilder.length());
        mTextView.setText("");
    }
}
