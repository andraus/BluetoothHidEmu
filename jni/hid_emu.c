
#include <hid_emu.h>

#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <bluetooth/sdp.h>
#include <bluetooth/sdp_lib.h>

sdp_session_t *sdp_session;
sdp_record_t *sdp_record = NULL;

static void add_lang_attr(sdp_record_t *r)
{
	sdp_lang_attr_t base_lang;
	sdp_list_t *langs = 0;

	/* UTF-8 MIBenum (http://www.iana.org/assignments/character-sets) */
	base_lang.code_ISO639 = (0x65 << 8) | 0x6e;
	base_lang.encoding = 106;
	base_lang.base_offset = SDP_PRIMARY_LANG_BASE;
	langs = sdp_list_append(0, &base_lang);
	sdp_set_lang_attr(r, langs);
	sdp_list_free(langs, 0);
}

/**
 * Creates the SDP record for HID emulation. This is basically a clone of
 * a PS3 wireless keypad record. Seems to work well with many different
 * clients: windows, linux (ubuntu), PS3, and motorola android devices.
 */
static sdp_record_t *create_hid_keyb_record()
{
	sdp_record_t *sdp_record;
	sdp_list_t *svclass_id, *pfseq, *apseq, *root;
	uuid_t root_uuid, hidkb_uuid, l2cap_uuid, hidp_uuid;
	sdp_profile_desc_t profile[1];
	sdp_list_t *aproto, *proto[3];
	sdp_data_t *channel, *lang_lst, *lang_lst2, *hid_spec_lst, *hid_spec_lst2;
	int i;
	uint8_t dtd = SDP_UINT16;
	uint8_t dtd2 = SDP_UINT8;
	uint8_t dtd_data = SDP_TEXT_STR8;
	void *dtds[2];
	void *values[2];
	void *dtds2[2];
	void *values2[2];
	int leng[2];
	uint8_t hid_spec_type = 0x22;
	uint16_t hid_attr_lang[] = {0x409,0x100};
	uint16_t value_int = 0;
	static const uint16_t ctrl = 0x11;
	static const uint16_t intr = 0x13;

	static const uint16_t hid_release_num = 0x100;
	static const uint16_t hid_parser_version = 0x111;
	static const uint8_t hid_dev_subclass = 0x40;
	static const uint8_t hid_country_code = 0x4;
	static const uint8_t hid_virtual_cable = 0x0;
	static const uint8_t hid_reconn_initiate = 0x1;

	static const uint8_t hid_sdp_disable = 0x0;
	static const uint8_t hid_batt_power = 0x1;
	static const uint8_t hid_remote_wake = 0x0;
	static const uint16_t hid_profile_version = 0x100;
	static const uint16_t hid_superv_timeout = 0x1f40;
	static const uint8_t hid_normally_connectable = 0x0;
	static const uint8_t hid_boot_device = 0x1;

	const uint8_t hid_spec[] = {
			0x05, 0x01,         /*  Usage Page (Desktop),                   */
			0x09, 0x06,         /*  Usage (Keyboard),                       */
			0xA1, 0x01,         /*  Collection (Application),               */
			0x85, 0x01,         /*      Report ID (1),                  */
			0x05, 0x07,         /*      Usage Page (Keyboard),              */
			0x19, 0xE0,         /*      Usage Minimum (KB Leftcontrol),     */
			0x29, 0xE7,         /*      Usage Maximum (KB Right GUI),       */
			0x15, 0x00,         /*      Logical Minimum (0),                */
			0x25, 0x01,         /*      Logical Maximum (1),                */
			0x75, 0x01,         /*      Report Size (1),                    */
			0x95, 0x08,         /*      Report Count (8),                   */
			0x81, 0x02,         /*      Input (Variable),                   */
			0x95, 0x01,         /*      Report Count (1),                   */
			0x75, 0x08,         /*      Report Size (8),                    */
			0x81, 0x01,         /*      Input (Constant),                   */
			0x95, 0x05,         /*      Report Count (5),                   */
			0x75, 0x01,         /*      Report Size (1),                    */
			0x05, 0x08,         /*      Usage Page (LED),                   */
			0x19, 0x01,         /*      Usage Minimum (01h),                */
			0x29, 0x05,         /*      Usage Maximum (05h),                */
			0x91, 0x02,         /*      Output (Variable),                  */
			0x95, 0x01,         /*      Report Count (1),                   */
			0x75, 0x03,         /*      Report Size (3),                    */
			0x91, 0x01,         /*      Output (Constant),                  */
			0x95, 0x06,         /*      Report Count (6),                   */
			0x75, 0x08,         /*      Report Size (8),                    */
			0x15, 0x00,         /*      Logical Minimum (0),                */
			0x26, 0xFF, 0x00,   /*      Logical Maximum (255),              */
			0x05, 0x07,         /*      Usage Page (Keyboard),              */
			0x19, 0x00,         /*      Usage Minimum (None),               */
			0x29, 0xE7,         /*      Usage Maximum (KB Right GUI),   */
			0x81, 0x00,         /*      Input,                              */
			0xC0,               /*  End Collection,                         */
			0x05, 0x0C,         /*  Usage Page (Consumer),                  */
			0x09, 0x01,         /*  Usage (Consumer Control),               */
			0xA1, 0x01,         /*  Collection (Application),               */
			0x85, 0x01,         /*      Report ID (1),                      */
			0x09, 0xE0,         /*      Usage (Volume),                     */
			0x15, 0xE8,         /*      Logical Minimum (-24),              */
			0x25, 0x18,         /*      Logical Maximum (24),               */
			0x75, 0x07,         /*      Report Size (7),                    */
			0x95, 0x01,         /*      Report Count (1),                   */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x15, 0x00,         /*      Logical Minimum (0),                */
			0x25, 0x01,         /*      Logical Maximum (1),                */
			0x75, 0x01,         /*      Report Size (1),                    */
			0x09, 0xE2,         /*      Usage (Mute),                       */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0xC0,               /*  End Collection,                         */
			0x05, 0x0C,         /*  Usage Page (Consumer),                  */
			0x09, 0x01,         /*  Usage (Consumer Control),               */
			0xA1, 0x01,         /*  Collection (Application),               */
			0x85, 0x03,         /*      Report ID (3),                      */
			0x15, 0x00,         /*      Logical Minimum (0),                */
			0x25, 0x01,         /*      Logical Maximum (1),                */
			0x75, 0x01,         /*      Report Size (1),                    */
			0x95, 0x01,         /*      Report Count (1),                   */
			0x0A, 0x27, 0x02,   /*      Usage (AC Refresh),                 */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x94, 0x01,   /*      Usage (AL Local Machine Brwsr),     */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x23, 0x02,   /*      Usage (AC Home),                    */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x8A, 0x01,   /*      Usage (AL Email Reader),            */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x92, 0x01,   /*      Usage (AL Calculator),              */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x26, 0x02,   /*      Usage (AC Stop),                    */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x25, 0x02,   /*      Usage (AC Forward),                 */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x24, 0x02,   /*      Usage (AC Back),                    */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x09, 0xB5,         /*      Usage (Scan Next Track),            */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x09, 0xB6,         /*      Usage (Scan Previous Track),        */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x09, 0xCD,         /*      Usage (Play Pause),                 */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x09, 0xB7,         /*      Usage (Stop),                       */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x0A, 0x83, 0x01,   /*      Usage (AL Consumer Control Config), */
			0x81, 0x06,         /*      Input (Variable, Relative),         */
			0x95, 0x0B,         /*      Report Count (11),                  */
			0x81, 0x01,         /*      Input (Constant),                   */
			0xC0,                /*  End Collection                          */

		    0x05, 0x01,                    // USAGE_PAGE (Generic Desktop)
		    0x09, 0x02,                    // USAGE (Mouse)
		    0xa1, 0x01,                    // COLLECTION (Application)
		    0x85, 0x02,         /*      Report ID (2),                  */
		    0x09, 0x01,                    //   USAGE (Pointer)
		    0xa1, 0x00,                    //   COLLECTION (Physical)
		    0x05, 0x09,                    //     USAGE_PAGE (Button)
		    0x19, 0x01,                    //     USAGE_MINIMUM (Button 1)
		    0x29, 0x03,                    //     USAGE_MAXIMUM (Button 3)
		    0x15, 0x00,                    //     LOGICAL_MINIMUM (0)
		    0x25, 0x01,                    //     LOGICAL_MAXIMUM (1)
		    0x95, 0x03,                    //     REPORT_COUNT (3)
		    0x75, 0x01,                    //     REPORT_SIZE (1)
		    0x81, 0x02,                    //     INPUT (Data,Var,Abs)
		    0x95, 0x01,                    //     REPORT_COUNT (1)
		    0x75, 0x05,                    //     REPORT_SIZE (5)
		    0x81, 0x03,                    //     INPUT (Cnst,Var,Abs)
		    0x05, 0x01,                    //     USAGE_PAGE (Generic Desktop)
		    0x09, 0x30,                    //     USAGE (X)
		    0x09, 0x31,                    //     USAGE (Y)
		    0x15, 0x81,                    //     LOGICAL_MINIMUM (-127)
		    0x25, 0x7f,                    //     LOGICAL_MAXIMUM (127)
		    0x75, 0x08,                    //     REPORT_SIZE (8)
		    0x95, 0x02,                    //     REPORT_COUNT (2)
		    0x81, 0x06,                    //     INPUT (Data,Var,Rel)
		    0xc0,                          //   END_COLLECTION
		    0xc0                           // END_COLLECTION
	};

	sdp_record = sdp_record_alloc();
	if (!sdp_record) {
		return NULL;
	}

	memset((void*)sdp_record, 0, sizeof(sdp_record_t));
	sdp_record->handle = 0xffffffff;
	sdp_uuid16_create(&root_uuid, PUBLIC_BROWSE_GROUP);
	root = sdp_list_append(0, &root_uuid);
	sdp_set_browse_groups(sdp_record, root);

	add_lang_attr(sdp_record);

	sdp_uuid16_create(&hidkb_uuid, HID_SVCLASS_ID);
	svclass_id = sdp_list_append(0, &hidkb_uuid);
	sdp_set_service_classes(sdp_record, svclass_id);

	sdp_uuid16_create(&profile[0].uuid, HID_PROFILE_ID);
	profile[0].version = 0x0100;
	pfseq = sdp_list_append(0, profile);
	sdp_set_profile_descs(sdp_record, pfseq);

	/* PROTO */
	sdp_uuid16_create(&l2cap_uuid, L2CAP_UUID);
	proto[1] = sdp_list_append(0, &l2cap_uuid);
	channel = sdp_data_alloc(SDP_UINT8, &ctrl);
	proto[1] = sdp_list_append(proto[1], channel);
	apseq = sdp_list_append(0, proto[1]);

	sdp_uuid16_create(&hidp_uuid, HIDP_UUID);
	proto[2] = sdp_list_append(0, &hidp_uuid);
	apseq = sdp_list_append(apseq, proto[2]);

	aproto = sdp_list_append(0, apseq);
	sdp_set_access_protos(sdp_record, aproto);

	/* ATTR_ADD_PROTO */
	proto[1] = sdp_list_append(0, &l2cap_uuid);
	channel = sdp_data_alloc(SDP_UINT8, &intr);
	proto[1] = sdp_list_append(proto[1], channel);
	apseq = sdp_list_append(0, proto[1]);

	sdp_uuid16_create(&hidp_uuid, HIDP_UUID);
	proto[2] = sdp_list_append(0, &hidp_uuid);
	apseq = sdp_list_append(apseq, proto[2]);

	aproto = sdp_list_append(0, apseq);
	sdp_set_add_access_protos(sdp_record, aproto);

	sdp_set_info_attr(sdp_record, "Android Bluetooth Keyboard",
		"", "HID device over Bluetooth for Android");

	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_DEVICE_RELEASE_NUMBER, SDP_UINT16, &hid_release_num);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_PARSER_VERSION, SDP_UINT16, &hid_parser_version);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_DEVICE_SUBCLASS, SDP_UINT8, &hid_dev_subclass);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_COUNTRY_CODE, SDP_UINT8, &hid_country_code);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_VIRTUAL_CABLE, SDP_BOOL, &hid_virtual_cable);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_RECONNECT_INITIATE, SDP_BOOL, &hid_reconn_initiate);

	dtds[0] = &dtd2;
	values[0] = &hid_spec_type;
	dtds[1] = &dtd_data;
	values[1] = (uint8_t*)hid_spec;
	leng[0] = 0;
	leng[1] = sizeof(hid_spec);
	hid_spec_lst = sdp_seq_alloc_with_length(dtds, values, leng, 2);
	hid_spec_lst2 = sdp_data_alloc(SDP_SEQ8, hid_spec_lst);
	sdp_attr_add(sdp_record, SDP_ATTR_HID_DESCRIPTOR_LIST, hid_spec_lst2);

	for (i = 0; i < sizeof(hid_attr_lang)/2; i++) {
		dtds2[i] = &dtd;
		values2[i] = &hid_attr_lang[i];
	}
	lang_lst = sdp_seq_alloc(dtds2, values2, sizeof(hid_attr_lang)/2);
	lang_lst2 = sdp_data_alloc(SDP_SEQ8, lang_lst);
	sdp_attr_add(sdp_record, SDP_ATTR_HID_LANG_ID_BASE_LIST, lang_lst2);

	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_SDP_DISABLE, SDP_BOOL, &hid_sdp_disable);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_BATTERY_POWER, SDP_BOOL, &hid_batt_power);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_REMOTE_WAKEUP, SDP_BOOL, &hid_remote_wake);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_PROFILE_VERSION, SDP_UINT16, &hid_profile_version);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_SUPERVISION_TIMEOUT, SDP_UINT16, &hid_superv_timeout);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_NORMALLY_CONNECTABLE, SDP_BOOL, &hid_normally_connectable);
	sdp_attr_add_new(sdp_record, SDP_ATTR_HID_BOOT_DEVICE, SDP_BOOL, &hid_boot_device);

	return sdp_record;
}

/**
 * Registers the new SDP record for HID, and returns the sdp_record_t *.
 */
sdp_record_t *sdp_register_hid() {
	sdp_record_t *rec = create_hid_keyb_record();

	int ret = -1;

	if ((ret = sdp_record_register(sdp_session, rec, SDP_RECORD_PERSIST)) < 0) {
		LOGE("%s: HID Device (Keyboard) Service Record registration failed\n", (char*)__func__);
		return NULL;
	}
	return rec;
}

/**
 * Retrieves SDP record identified by "handle". Session must be already opened.
 */
sdp_record_t *get_sdp_record(sdp_session_t *session, int handle) {
	sdp_list_t *attrid;
	uint32_t range = 0x0000ffff;
	sdp_record_t *rec;

	attrid = sdp_list_append(0, &range);
	rec = sdp_service_attr_req(session, handle, SDP_ATTR_REQ_RANGE, attrid);
	sdp_list_free(attrid,0);

	return rec;
}

/**
 * Removes SDP record identified by "handle".
 */
void sdp_remove(int handle) {

	if (sdp_session) {
		sdp_record_t * rec = get_sdp_record(sdp_session, handle);
		if (rec != NULL && sdp_record_unregister(sdp_session, rec)) {
			LOGE("%s: HID Device (Keyboard) Service Record unregistration failed\n", (char*)__func__);
		}
	}

}

/**
 * Opens sdp_session. Note: sdp_session has global scope
 */
int sdp_open()
{
	if (!sdp_session) {
		sdp_session = sdp_connect(BDADDR_ANY, BDADDR_LOCAL, SDP_NON_BLOCKING);
	}
	if (!sdp_session) {
		LOGE("%s: sdp_session invalid: %s (%d)\n", (char*)__func__, strerror(errno), errno);
		return -1;
	}
	return 0;
}


/**
 * Lookup for HID service records. This function returns:
 * - SDP HID record handle, if there is already a HID SDP record
 * 0 if there is no HID SDP record
 * <0 if any error occur. In this case, the value returned is the errno.
 *
 */
int is_hid_sdp_record_registered() {
	int handle;
	uuid_t svc_uuid;
	int err;
	sdp_list_t *response_list = NULL, *search_list, *attrid_list;

	sdp_uuid16_create(&svc_uuid, HID_SVCLASS_ID);
	search_list = sdp_list_append(NULL, &svc_uuid);

	uint32_t range = 0x0000ffff;
	attrid_list = sdp_list_append(NULL, &range);

	err = sdp_service_search_attr_req(sdp_session, search_list, SDP_ATTR_REQ_RANGE, attrid_list, &response_list);

	sdp_list_free(search_list, NULL);
	sdp_list_free(attrid_list, NULL);

	if (err < 0) {
		return err;
	}

	if (response_list != NULL) {
		sdp_record_t *rec = (sdp_record_t *)response_list->data;
		int handle = rec->handle;
		sdp_list_free(response_list, NULL);
		return handle;
	} else {
		return 0;
	}


	/*
	 * code below illustrates how to iterate through records.
	 *
	sdp_list_t *r = response_list;
	for (; r; r = r->next) {
		sdp_record_t *rec = (sdp_record_t *)r->data;

		if (rec != NULL) {
			printf("rec: 0x%X\n", rec->handle);
		}
		sdp_record_free(rec);
	}
	*/
}

int add_hid() {

	int handle = 0x0;
	int ret = sdp_open();

	if (ret == 0) {
		if ((handle = is_hid_sdp_record_registered()) == 0) {
			sdp_record = sdp_register_hid();
			if (sdp_record == NULL) {
				LOGE("Error register sdp record: %d\n", ret);
				sdp_close(sdp_session);
				return ret;
			}
		} else {
			LOGD("Nothing done; already registered\n");
			sdp_close(sdp_session);
			return handle;
		}
		sdp_close(sdp_session);
	}
	return sdp_record->handle;

}

/**
 * Reads the device class. cls must be a uint8[3].
 */
int read_device_class(int hdev, uint8_t *cls) {
	int s = hci_open_dev(hdev);
	if (s < 0) {
		LOGE("Cannot open device hci%d: %s (%d)\n", hdev, strerror(errno), errno);
		return errno;
	}

	if (hci_read_class_of_dev(s, cls, 1000) < 0) {
		LOGE("Cannot read class of device hci%d: %s (%d)\n", hdev, strerror(errno), errno);
		return errno;
	}
	return 0;
}

/**
 * Spoofs the device class. cls is a string in the format 0xffffff.
 */
int spoof_device_class(int hdev, char *cls) {
	int s = hci_open_dev(hdev);
	if (s < 0) {
		LOGE("Cannot open device hci%d: %s (%d)\n", hdev, strerror(errno), errno);
		return errno;
	}
	uint32_t cod = strtoul(cls, NULL, 16);
	if (hci_write_class_of_dev(s, cod, 2000) < 0) {
		LOGE("Cannor write class for hci%d: %s(%d)\n", hdev, strerror(errno), errno);
		return errno;
	}
	return 0;

}


/**
 * Usage:
 *
 * add a HID sdp record:
 * 	- hid_emu add_hid
 *
 * remove a SDP record handle:
 *  - hid_emu del_hid <handle>
 *
 * read device class:
 *  - hid_emu read_class
 *
 * spoof device class:
 *  - hid_emu spoof_class <class>
 *
 */

const int ADD_HID_ARGS = 2;
const char *ADD_HID = "add_hid";
const int DEL_HID_ARGS = 3;
const char *DEL_HID = "del_hid";
const int READ_CLASS_ARGS = 2;
const char *READ_CLASS = "read_class";
const int SPOOF_CLASS_ARGS = 3;
const char *SPOOF_CLASS = "spoof_class";


int main(int argc, char *argv[]) {
	int i;

	if (argc == 1) {
		printf("version : %s\n", HIDEMU_VERSION);
		return 0;
	}

	if(hci_get_route(NULL)  < 0) {
		printf("Bluetooth off\n");
		return -1;
	}

	if (DBG_CONSOLE) {

		for (i = 0; i < argc; i++) {
			printf("arg[%d]=%s\n", i, argv[i]);
		}

	}

	if (argc == ADD_HID_ARGS && (strncmp(argv[1], ADD_HID, strlen(argv[1])) == 0)) {
		int handle = add_hid();
		printf("handle: 0x%X\n", handle);
		if (sdp_record != NULL) {
			sdp_record_free(sdp_record);
		}

	} else if (argc == DEL_HID_ARGS && (strncmp(argv[1], DEL_HID, strlen(argv[1])) == 0) && !strncasecmp(argv[2], "0x", 2)) {
		int handle = strtol(argv[2]+2, NULL, 16);
		printf("Removed handle: 0x%X\n", handle);
		if (!sdp_session) {
			sdp_open(sdp_session);
		}
		sdp_remove(handle);
		sdp_close(sdp_session);

	} else if (argc == READ_CLASS_ARGS && (strncmp(argv[1], READ_CLASS, strlen(argv[1])) == 0)) {
		uint8_t cls[3];
		if (read_device_class(0, cls) == 0) {
			printf("class: 0x%02x%02x%02x\n", cls[2], cls[1], cls[0]);
		}

	} else if (argc == SPOOF_CLASS_ARGS && (strncmp(argv[1], SPOOF_CLASS, strlen(argv[1])) == 0)) {
		if (spoof_device_class(0, argv[2]) == 0) {
			printf("class spoofed.\n");
		}

	} else {
		printf("Invalid arguments\n");
		LOGE("Invalid arguments\n");
	}

	return 0;

}
