#ifndef __HID_EMU_H__
#define __HID_EMU_H__

#define LOG_DEBUG 0
#define LOG_NDDEBUG 0
#define LOG_NIDEBUG 0

#define HIDEMU_VERSION "1.03"

#define LOG_TAG "HidEmu_jni"

#define HID_MODE_GENERIC 0
#define HID_MODE_PS3KEYPAD 1
#define HID_MODE_BDREMOTE 2

#include "cutils/logger.h"
#include "cutils/logprint.h"

/* set DBG_CONSOLE to 1 to print debug messages to console */
#define DBG_CONSOLE 0

/* enabling $define below will print error messages to console */
/* #define LOGE(...) prinf(...) */

#endif
