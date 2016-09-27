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
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Second Activity
 *
 * @author venshine
 */
public class Activity2 extends BaseActivity {

    private static StringBuilder mBuilder = new StringBuilder();
    private TextView mTextView;
    private ScrollView mScrollView;
    private TextView mDrawTextView;
    private ImageView mDrawImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity2);
        setTitle("Second Activity");
        mTextView = (TextView) findViewById(R.id.textview);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        mDrawTextView = (TextView) findViewById(R.id.drag_textview);
        mDrawImageView = (ImageView) findViewById(R.id.drag_imageview);
        mDrawTextView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        log("ACTION_DRAG_STARTED");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        log("ACTION_DRAG_ENTERED");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        log("ACTION_DRAG_EXITED");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        log("ACTION_DRAG_LOCATION");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        log("ACTION_DRAG_ENDED");
                        break;
                    case DragEvent.ACTION_DROP:
                        log("ACTION_DROP");
                        mDrawTextView.setVisibility(View.GONE);
                        mDrawImageView.setVisibility(View.VISIBLE);
                        ClipData clipData = dragEvent.getClipData();
                        mDrawImageView.setImageURI(clipData.getItemAt(1).getUri());
                        Toast.makeText(mActivity, clipData.getItemAt(0).getText(), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
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
     * 打印富媒体信息
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
     * 清理日志
     *
     * @param v
     */
    public void clear(View v) {
        mBuilder.delete(0, mBuilder.length());
        mTextView.setText("");
        mDrawImageView.setVisibility(View.GONE);
        mDrawTextView.setVisibility(View.VISIBLE);
    }
}
