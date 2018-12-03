# this is included by mkvendor.sh
# lets just leave it here :>
USE_CAMERA_STUB := true

# *most* are referred from kyasu's gt-i9506 device tree. Props!

# inherit from common-mt6735
# -include device/samsung/mt6735-common/BoardConfigCommon.mk
# nope :)

# Build with Clang by default
# USE_CLANG_PLATFORM_BUILD := true
# i don't know. build recovery w/ this take 15min / 8min w/ jack

# current kernel from samsung opensource is VERY outdated
# Prebuilt
# TARGET_PREBUILT_KERNEL := device/samsung/grandppltedx/kernel
# because im dumb. ramdisk contents is defined by rootdir contents

# kernel stuff
# 	use kernel source. prop2 Sub for L.OS defconfig
# 	Though i don't know if it's any better :3
TARGET_KERNEL_SOURCE := kernel/samsung/grandppltedx
TARGET_KERNEL_CONFIG := mt6737t-grandppltedx-lineageos_defconfig

# is 32-bit
FORCE_32_BIT := true

# set local-path
LOCAL_PATH := device/samsung/grandppltedx
TARGET_SPECIFIC_HEADER_PATH := $(LOCAL_PATH)/include
TARGET_OTA_ASSERT_DEVICE := grandpplte,grandppltedx,G532G,G532G/DS

# Bootloader
TARGET_BOOTLOADER_BOARD_NAME := mt6737t

# CFLAGS
BOARD_GLOBAL_CFLAGS += -DNO_SECURE_DISCARD
BOARD_GLOBAL_CFLAGS += -DDISABLE_HW_ID_MATCH_CHECK
BOARD_GLOBAL_CFLAGS += -DADD_LEGACY_ACQUIRE_BUFFER_SYMBOL

# RIL
BOARD_RIL_CLASS := ../../../device/samsung/grandppltedx/ril

#custom bootimg
BOARD_CUSTOM_BOOTIMG := true

#Target arch
TARGET_BOARD_PLATFORM := mt6737t
TARGET_ARCH := arm
TARGET_NO_BOOTLOADER := true
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_VARIANT := cortex-a53
TARGET_CPU_SMP := true
ARCH_ARM_HAVE_TLS_REGISTER := true

#MTK Hardware
BOARD_HAS_MTK_HARDWARE := true
BOARD_USES_MTK_HARDWARE := true
MTK_HARDWARE := true
BOARD_USES_MTK_MEDIA_PROFILES := true

#hmm?
#MTK_GPU_VERSION := mali midgard m7p0

# Kernel
BOARD_KERNEL_CMDLINE := bootopt=64S3,32N2,32N2
# why permissive on SELinux-enforcing device?
BOARD_KERNEL_BASE := 0x3fffc000
BOARD_RAMDISK_OFFSET = 0x04004000
BOARD_TAGS_OFFSET = 0xE004000
BOARD_KERNEL_OFFSET = 0x00008000
BOARD_KERNEL_PAGESIZE := 2048

BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE) --pagesize 2048 --kernel_offset $(BOARD_KERNEL_OFFSET) --ramdisk_offset $(BOARD_RAMDISK_OFFSET) --tags_offset $(BOARD_TAGS_OFFSET) --dt $(LOCAL_PATH)/dt.img --board SRPPI01A000KU

# fix this up by examining --/proc/mtd-- on a running device
# nope, /proc/partitions
# used from reference
# Credit to Sub_Zero2 @ xda
# FS
TARGET_USERIMAGES_USE_EXT4 := true
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
BOARD_BOOTIMAGE_PARTITION_SIZE := 16777216
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 16777216
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3229614080
BOARD_USERDATAIMAGE_PARTITION_SIZE := 3900702720
BOARD_CACHEIMAGE_PARTITION_SIZE := 419430400
BOARD_FLASH_BLOCK_SIZE := 4096

# Power HAL
TARGET_POWERHAL_VARIANT := mtk
TARGET_POWERHAL_SET_INTERACTIVE_EXT := $(LOCAL_PATH)/power/power.c

# Wifi
# BOARD_HAVE_SAMSUNG_WIFI := true
# what da hek? - mt6735 got what i needed

# Display
TARGET_SCREEN_HEIGHT := 960
TARGET_SCREEN_WIDTH := 540
# should force DPI?

# Recovery
#RECOVERY_VARIANT := twrp

# inherit from the proprietary version
#-include vendor/samsung/grandppltedx/BoardConfigVendor.mk

# I don't really know the purpose?
BOARD_HAS_NO_SELECT_BUTTON := true
