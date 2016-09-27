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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Base Activity
 *
 * @author venshine
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        print("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        print("onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        print("onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        print("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        print("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        print("onDestory", "#0000ff", false);
        print("");
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        print("InMultiWindowMode:" + isInMultiWindowMode, null, true);
    }

    protected abstract void print(String msg);

    protected abstract void print(String msg, String color, boolean bold);

    public void log(String msg) {
        Log.d("venshine", msg);
    }

    /**
     * Convert uri by drawable id.
     * Uri uri = Uri.parse("android.resource://com.wx.multiwindow/mipmap/ic_launcher");
     *
     * @param context
     * @param id
     * @return
     */
    public Uri getUri(Context context, int id) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getResources().getResourcePackageName(id) + "/"
                + context.getResources().getResourceTypeName(id) + "/"
                + context.getResources().getResourceEntryName(id));
    }

}
