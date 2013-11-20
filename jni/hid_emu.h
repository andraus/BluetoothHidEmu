#ifndef __HID_EMU_H__
#define __HID_EMU_H__

#include <stdint.h>
#include <errno.h>
#include <android/log.h>

#define HIDEMU_VERSION "1.04"

#define LOG_TAG "HidEmu_jni"

#define HID_MODE_GENERIC 0
#define HID_MODE_PS3KEYPAD 1
#define HID_MODE_BDREMOTE 2

#define DBG_CONSOLE 0

#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))


int read_device_class(int hdev, uint8_t *cls);

#endif
