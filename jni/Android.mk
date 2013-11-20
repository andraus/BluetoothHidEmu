LOCAL_PATH := $(call my-dir)

###############################################
#  libbluetooth.so from MB632
##############################################

include $(CLEAR_VARS)

LOCAL_MODULE := lib-bluetooth
LOCAL_SRC_FILES := lib/libbluetooth.so
LOCAL_C_INCLUDES := $(LOCAL_PATH)/jni/bluez

include $(PREBUILT_SHARED_LIBRARY)

###############################################
#  bluetoothsocket lib
##############################################

include $(CLEAR_VARS)

LOCAL_MODULE := bluetoothsocket

LOCAL_SRC_FILES := hid_emu.c \
                   bluetooth_socket.c

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/bluez

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := lib-bluetooth

include $(BUILD_SHARED_LIBRARY)

##############################################
# makefile for hid_emu
#############################################

include $(CLEAR_VARS)

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/bluez

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE    := hid_emu
LOCAL_SRC_FILES :=  hid_emu.c

LOCAL_CERTIFICATE := platform
LOCAL_PRELINK_MODULE := false

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := lib-bluetooth

LOCAL_DEFAULT_CPP_EXTENSION := cpp

include $(BUILD_EXECUTABLE)
