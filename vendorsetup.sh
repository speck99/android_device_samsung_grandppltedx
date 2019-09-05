#!/bin/bash
cd frameworks/native
git reset --hard && git clean -f -d
patch -p1 < ../../device/samsung/grandpplte/patches/0002-frameworks-native.patch
cd ../..
cd system/core
git reset --hard && git clean -f -d
patch -p1 < ../../device/samsung/grandpplte/patches/0003-init-HACK-re-allow-services-without-selinux-contexts-defined.patch
patch -p1 < ../../device/samsung/grandpplte/patches/0004-libnetutils-add-MTK-bits-ifc_ccmni_md_cfg.patch
add_lunch_combo lineage_grandppltedx-userdebug
add_lunch_combo lineage_grandppltedx-eng

# 16GB-single
