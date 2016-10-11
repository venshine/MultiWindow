# Android N 多窗口支持

[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

**开发文档：**[https://github.com/venshine/MultiWindow/wiki](https://github.com/venshine/MultiWindow/wiki)

## 多窗口模式

多窗口模式有两种，分屏模式和自由形状模式。

### 分屏模式

长按Overview按钮进入到分屏模式。

![]((https://raw.githubusercontent.com/venshine/MultiWindow/master/image/screenshot1.png))

### 自由形状模式

开启自由形状模式步骤如下：

* 打开模拟器或者用usb线连接已root的设备（前提是要root）
* 命令行（终端）中输入adb shell
* 然后输入su命令，获取root权限授权（此步要在手机上允许授权）
* 输入命令代码 settings put global enable_freeform_support 1
* 进入设置-开发者选项，然后滑动到界面最底部，打开强制将活动设为可调整大小按钮
* 重启模拟器或设备

![]((https://raw.githubusercontent.com/venshine/MultiWindow/master/image/screenshot2.png))

## 简易教程

### 禁用多窗口模式
该属性为true时将支持多窗口模式，为false时不支持，以全屏模式展示。（该属性默认值为true）
```
    android:resizeableActivity=["true" | "false"]
```

### 多窗口变更通知和查询
```
    Activity.isInMultiWindowMode()    // 调用该方法以确认 Activity 是否处于多窗口模式

    Activity.onMultiWindowModeChanged()     // Activity 进入或退出多窗口模式时系统将调用此方法
```

### 在多窗口模式中启动新 Activity
当满足下面的条件，系统会让这两个Activity进入分屏模式：
* 当前Activity已经进入到分屏模式。
* 新打开的Activity支持分屏浏览（即android:resizeableActivity=true）。
```
    Intent intent = new Intent(this, SecondActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
```

### 为多窗口模式准备的布局属性
```
    android:defaultWidth   // 以自由形状模式启动时 Activity 的默认宽度
    android:defaultHeight  // 以自由形状模式启动时 Activity 的默认高度
    android:gravity        // 以自由形状模式启动时 Activity 的初始位置
    android:minHeight、android:minWidth    // 分屏和自由形状模式中 Activity 的最小高度和最小宽度。 如果用户在分屏模式中移动分界线，使 Activity 尺寸低于指定的最小值，系统会将 Activity 裁剪为用户请求的尺寸 
```

### 支持拖放
发起拖放事件（[FirstActivity](https://github.com/venshine/MultiWindow/blob/master/app/src/main/java/com/wx/multiwindow/FirstActivity.java)）
```
    mImageView.setTag("Drag an ImageView from First Activity");
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View view) {
                Uri uri = getUri(mActivity, R.mipmap.ic_launcher);
                ClipData dragData = ClipData.newPlainText("", (CharSequence) view.getTag());
                ClipData.Item imageItem = new ClipData.Item(uri);
                dragData.addItem(imageItem);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                view.startDragAndDrop(dragData, shadow, view, View.DRAG_FLAG_GLOBAL);
                return true;
            }
        });
```

接收拖放结果（[SecondActivity](https://github.com/venshine/MultiWindow/blob/master/app/src/main/java/com/wx/multiwindow/SecondActivity.java)）
```
        mDrawTextView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DROP:
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
```

## 参考
* [开发文档](https://github.com/venshine/MultiWindow/wiki)
* [官方Sample](https://github.com/googlesamples/android-MultiWindowPlayground)

## About
* Email：venshine.cn@gmail.com

## License
    Copyright (C) 2016 venshine.cn@gmail.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




