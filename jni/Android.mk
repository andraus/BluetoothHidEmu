LOCAL_PATH := $(call my-dir)
base_path := $(LOCAL_PATH)/../../../../..

include $(CLEAR_VARS)

#LOCAL_CFLAGS := -O3 -g -W -Wall
	
#    $(base_path)/system/bluetooth/bluedroid/include # 	$(base_path)/system/bluetooth/bluez-clean-headers

LOCAL_C_INCLUDES := \
 	$(base_path)/external/bluetooth/bluez/lib \
 	$(base_path)/external/bluetooth/bluez/common

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE    := hid_emu
LOCAL_SRC_FILES :=  hid_emu.c

LOCAL_CERTIFICATE := platform
LOCAL_PRELINK_MODULE := false

LOCAL_SHARED_LIBRARIES := liblog libbluetoothd libbluetooth 	

LOCAL_DEFAULT_CPP_EXTENSION := cpp

include $(BUILD_EXECUTABLE)
