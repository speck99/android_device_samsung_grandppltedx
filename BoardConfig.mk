#
# Copyright (C) 2012 The CyanogenMod Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

DEVICE_PATH := device/samsung/grandppltedx
BOARD_VENDOR := samsung

# assert
TARGET_OTA_ASSERT_DEVICE := grandpplte,grandppltedx,grandpplteub,grandpplteser,grandppltedtvvj

# Bootloader
TARGET_BOOTLOADER_BOARD_NAME := MT6737T
TARGET_NO_BOOTLOADER := true

# Platform
TARGET_INIT_VENDOR_LIB := libinit_grandpplte

TARGET_BOARD_PLATFORM := MT6737T

USE_CAMERA_STUB := true

# Backlight
BACKLIGHT_PATH := /sys/class/leds/lcd-backlight/brightness

# Charger (borrow from herolte)
BOARD_CHARGING_MODE_BOOTING_LPM := /sys/class/power_supply/battery/batt_lp_charging
BOARD_CHARGER_ENABLE_SUSPEND := true
BOARD_CHARGER_SHOW_PERCENTAGE := true
CHARGING_ENABLED_PATH := /sys/class/power_supply/battery/batt_lp_charging

# Architecture
TARGET_ARCH         := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI      := armeabi-v7a
TARGET_CPU_ABI2     := armeabi
TARGET_CPU_VARIANT  := cortex-a53
TARGET_CPU_SMP      := true

# Block
BOARD_BOOTIMAGE_PARTITION_SIZE        := 16777216
BOARD_RECOVERYIMAGE_PARTITION_SIZE    := 16777216
BOARD_SYSTEMIMAGE_PARTITION_SIZE      := 3229614080
BOARD_CACHEIMAGE_PARTITION_SIZE       := 419430400
BOARD_FLASH_BLOCK_SIZE                := 4096

BOARD_USERDATAIMAGES_FILE_SYSTEM_TYPE := ext4 
BOARD_SYSTEMIMAGE_FILE_SYSTEM_TYPE    := ext4
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE     := ext4

TARGET_USERIMAGES_USE_EXT4 := true

BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(DEVICE_PATH)/bluetooth

# CMHW
BOARD_USES_LINEAGE_HARDWARE := true
#BOARD_USES_CYANOGEN_HARDWARE := true
BOARD_HARDWARE_CLASS += $(DEVICE_PATH)/lineagehw

# RIL
# use prebuilt for now

BOARD_RIL_CLASS := ../../../device/samsung/grandppltedx/ril

# Wifi
WPA_SUPPLICANT_VERSION := VER_0_8_X
BOARD_HOSTAPD_DRIVER := NL80211
BOARD_HOSTAPD_PRIVATE_LIB := lib_driver_cmd_mt66xx
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_mt66xx
WIFI_DRIVER_FW_PATH_PARAM := /dev/wmtWifi
WIFI_DRIVER_FW_PATH_AP := AP
WIFI_DRIVER_FW_PATH_STA := STA
WIFI_DRIVER_FW_PATH_P2P := P2P
WIFI_DRIVER_STATE_CTRL_PARAM := /dev/wmtWifi

# Kernel
BOARD_KERNEL_CMDLINE  := bootopt=64S3,32N2,32N2 androidboot.selinux=permissive androidboot.selinux=disabled
BOARD_KERNEL_BASE     := 0x3fffc000
BOARD_KERNEL_PAGESIZE := 2048
BOARD_RAMDISK_OFFSET  := 0x04004000
BOARD_SECOND_OFFSET   := 0x00f04000
BOARD_TAGS_OFFSET     := 0x0e004000
TARGET_KERNEL_ARCH := arm
BOARD_KERNEL_OFFSET   := 0x00008000
BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE) --pagesize $(BOARD_KERNEL_PAGESIZE) --kernel_offset $(BOARD_KERNEL_OFFSET) --ramdisk_offset $(BOARD_RAMDISK_OFFSET) --second_offset $(BOARD_SECOND_OFFSET) --tags_offset $(BOARD_TAGS_OFFSET) --board $(TARGET_BOOTLOADER_BOARD_NAME) --dt $(DEVICE_PATH)/dt.img
TARGET_PREBUILT_KERNEL := $(DEVICE_PATH)/prebuilt/zImage

# system properties
TARGET_SYSTEM_PROP += $(DEVICE_PATH)/system.prop

# SEAndroid
include $(DEVICE_PATH)/sepolicy/sepolicy.mk
BOARD_SECCOMP_POLICY += $(DEVICE_PATH)/seccomp

# Hack for building without kernel sources
$(shell mkdir -p $(OUT)/obj/KERNEL_OBJ/usr)