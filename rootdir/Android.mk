LOCAL_PATH := device/samsung/grandppltedx/rootdir

define add-rootdir-targets
$(foreach target,$(1), \
   $(eval include $(CLEAR_VARS)) \
   $(eval LOCAL_MODULE       := $(target)) \
   $(eval LOCAL_MODULE_CLASS := ETC) \
   $(eval LOCAL_SRC_FILES    := $(target)) \
   $(eval LOCAL_MODULE_PATH  := $(TARGET_ROOT_OUT)) \
   $(eval include $(BUILD_PREBUILT)))
endef

$(call add-rootdir-targets, \
	fstab.mt6735 \
	factory_init.rc \
	factory_init.project.rc \
	meta_init.rc \
	meta_init.modem.rc \
	meta_init.project.rc \
	meta_init.usb.rc \
	init.modem.rc \
	init.mt6735.rc \
	init.mt6735.usb.rc \
	init.project.rc \
	init.recovery.mt6735.rc \
	init.rilchip.rc \
	init.rilepdg.rc \
	init.rilcommon.rc \
	init.usb.configfs.rc \
	init.volte.rc \
	init.wifi.rc \
	ueventd.mt6735.rc \
	init.samsung.rc)
