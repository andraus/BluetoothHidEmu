#include "bluetooth_socket.h"
#include "hid_emu.h"

#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/l2cap.h>

/**
 *
 */
JNIEXPORT jint JNICALL Java_andraus_bluetoothhidemu_spoof_jni_BluetoothSocketJni_createL2capFileDescriptor
  (JNIEnv *env, jclass jc, jboolean auth, jboolean encrypt) {

    int fd;
    int lm = 0;

    fd = socket(PF_BLUETOOTH, SOCK_SEQPACKET, BTPROTO_L2CAP);

    if (fd < 0) {
		LOGE("%s: Socket error: %s (%d)\n", (char*)__func__, strerror(errno), errno);
		return -1;
    }

    lm |= auth ? L2CAP_LM_AUTH : 0;
    lm |= encrypt ? L2CAP_LM_ENCRYPT : 0;
    lm |= (auth && encrypt) ? L2CAP_LM_SECURE : 0;

    if (setsockopt(fd, SOL_L2CAP, L2CAP_LM, &lm, sizeof(lm))) {
		LOGE("%s: Setting socket options error: %s (%d)\n", (char*)__func__, strerror(errno), errno);
		return -1;
    }

    return fd;

  }

/**
 *
 */
JNIEXPORT jintArray JNICALL Java_andraus_bluetoothhidemu_spoof_jni_BluetoothSocketJni_readBluetoothDeviceClass
  (JNIEnv *env, jclass jc) {

    jintArray result = NULL;
    uint8_t clazz[3];

    if (read_device_class(0, clazz) == 0) {

        result = (*env)->NewIntArray(env, 3);
        jint *resultArray = (*env)->GetIntArrayElements(env, result, 0);

        resultArray[2] = clazz[2];
        resultArray[1] = clazz[1];
        resultArray[0] = clazz[0];

        (*env)->ReleaseIntArrayElements(env, result, resultArray, 0);
    }

    return result;

  }
