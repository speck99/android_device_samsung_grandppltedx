# Release name
PRODUCT_RELEASE_NAME := grandppltedx

# Inherit some common CM stuff.
$(call inherit-product, vendor/cm/config/common_full_phone.mk)

# Inherit device configuration
$(call inherit-product, device/samsung/grandppltedx/device_grandppltedx.mk)

## Device identifier. This must come after all inclusions
PRODUCT_DEVICE := grandppltedx
PRODUCT_NAME := cm_grandppltedx
PRODUCT_BRAND := samsung
PRODUCT_MODEL := grandppltedx
PRODUCT_MANUFACTURER := samsung
