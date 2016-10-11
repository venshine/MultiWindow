# Android N 多窗口支持

[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

学习教程：[https://github.com/venshine/MultiWindow/wiki](https://github.com/venshine/MultiWindow/wiki)

## 多窗口模式

多窗口模式有两种，分屏模式和自由形状模式。

### 分屏模式

长按Overview按钮进入到分屏模式。

### 自由形状模式

开启自由形状模式步骤如下：
1.打开模拟器或者用usb线连接已root的设备（前提是要root）
2.命令行（终端）中输入adb shell
3.然后输入su命令，获取root权限授权（此步要在手机上允许授权）
4.输入命令代码 settings put global enable_freeform_support 1
5.进入设置-开发者选项，然后滑动到界面最底部，打开强制将活动设为可调整大小按钮
6.重启模拟器或设备

## 参考


