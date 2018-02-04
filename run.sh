#!/bin/bash
tar -xvf $1
brand=$(cat build.prop | grep ro.product.brand= | cut -d = -f 2)
codename=$(cat build.prop | grep ro.build.product= | cut -d = -f 2)
model=$(cat build.prop | grep ro.product.model= | cut -d = -f 2)
platform=$(cat build.prop | grep ro.board.platform= | cut -d = -f 2)
recoverySize=$(wc -c < recovery.img)

mkAndroid()
{
cat << EOF
#
# Copyright (C) 2018 The TwrpBuilder Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ifneq (\$(filter $codename,\$(TARGET_DEVICE)),)

LOCAL_PATH := device/$brand/$codename

include \$(call all-makefiles-under,\$(LOCAL_PATH))

endif
EOF
}

mkBoardConfig(){
cat << EOF
#
# Copyright (C) 2018 The TwrpBuilder Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := device/$brand/$codename

TARGET_BOARD_PLATFORM := $platform
TARGET_BOOTLOADER_BOARD_NAME := $codename
BOARD_RECOVERYIMAGE_PARTITION_SIZE := $recoverySize 

# Recovery
TARGET_USERIMAGES_USE_EXT4 := true

EOF
}

mkAndroidProducts()
{
cat << EOF
#
# Copyright (C) 2018 The TwrpBuilder Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
PRODUCT_MAKEFILES := \$(LOCAL_PATH)/omni_$codename.mk
EOF
}

mkOmni()
{
cat << EOF
#
# Copyright (C) 2018 The TwrpBuilder Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
\$(call inherit-product, \$(SRC_TARGET_DIR)/product/full_base.mk)

PRODUCT_COPY_FILES += device/$brand/$codename/kernel:kernel

PRODUCT_DEVICE := $codename
PRODUCT_NAME := omni_$codename
PRODUCT_BRAND := $brand
PRODUCT_MODEL := $model
PRODUCT_MANUFACTURER := $brand
EOF
}

mkdir $codename
cd $codename
mkBoardConfig > BoardConfig.mk
mkAndroid > Android.mk
mkAndroidProducts > AndroidProducts.mk
mkOmni > omni_$codename.mk
cd ..
./mkKernel.sh

