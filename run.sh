#!/bin/bash


if [ -z $1 ]
then
echo "Please enter file name"
exit
elif [[ $1 == "-h" || $1 == "--help" ]]
then
echo "usage:- $0 input_file.tar.gz "
exit
elif [ ! -f $1 ]
then
echo "Please enter correct file name"
exit
fi

rooted=$(file --mime-type $1 | grep -w 'gzip'  | cut -d / -f 2 | cut -d "-" -f 2)
non_rooted=$(file --mime-type $1 | grep -w 'zip'  | cut -d / -f 2 | cut -d "-" -f 2)

if [ ! -z "$non_rooted" ]
then
unzip $1
sed 's/\[\([^]]*\)\]/\1/g' build.prop | sed 's/: /=/g' | tee > b.prop
rm build.prop
mv b.prop build.prop
rm $1
tar -czvf $1 build.prop recovery.img
echo "Making tree for non rooted device"
elif [ ! -z "$rooted" ]
then
echo "Making tree for rooted device"
tar -xvf $1
else 
echo "UnSupported"
exit
fi

export brand=$(cat build.prop | grep ro.product.brand= | cut -d = -f 2)
export codename=$(cat build.prop | grep ro.build.product= | cut -d = -f 2)
export model=$(cat build.prop | grep ro.product.model= | cut -d = -f 2)
export platform=$(cat build.prop | grep ro.board.platform= | cut -d = -f 2)
export abi=$(cat build.prop | grep ro.product.cpu.abi= | cut -d = -f 2)
export recoverySize=$(wc -c < recovery.img)

if [ -d $codename ]
then
rm -rf $codename
fi

if [ -d out ]
then
rm -rf out
fi

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

# Recovery
TARGET_USERIMAGES_USE_EXT4 := true
BOARD_RECOVERYIMAGE_PARTITION_SIZE := $recoverySize 
BOARD_FLASH_BLOCK_SIZE := 1000000
BOARD_HAS_NO_REAL_SDCARD := true
TW_EXCLUDE_SUPERSU := true

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

LOCAL_PATH := device/$brand/$codename

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

## Make Kernel


mkdir out
./umkbootimg -i recovery.img -o out/ &> out/output.txt
cd out
checkdtSize=$(cat output.txt | grep BOARD_DT_SIZE | cut -d ">" -f 2)
pagesize=$(cat recovery.img-pagesize)
cmdline=$(cat recovery.img-cmdline)
ramdiskofsset=$(cat recovery.img-ramdisk_offset)
tagsoffset=$(cat recovery.img-tags_offset)
kernelbase=$(cat recovery.img-base)
cd ..

mkdt()
{
if [ $checkdtSize == 0 ]
then
echo "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x$ramdiskofsset --tags_offset 0x$tagsoffset"
else
echo "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x$ramdiskofsset --tags_offset 0x$tagsoffset --dt device/$brand/$codename/dt.img"
cp out/recovery.img-dt $codename/dt.img
fi
}

mkKernelInfo(){
cat << EOF

# Kernel
TARGET_PREBUILT_KERNEL := device/$brand/$codename/kernel
BOARD_KERNEL_CMDLINE := $cmdline androidboot.selinux=permissive
BOARD_KERNEL_BASE := 0x$kernelbase
BOARD_KERNEL_PAGESIZE := $pagesize
EOF
mkdt
}

mkKernelInfo >> $codename/BoardConfig.mk
cp out/recovery.img-zImage $codename/kernel

## Make Arch

mkArch32()
{
cat << EOF

include device/generic/twrpbuilder/BoardConfig32.mk
EOF
}

mkArch64()
{
cat << EOF

include device/generic/twrpbuilder/BoardConfig64.mk
EOF
}

if [ $abi == "armeabi-v7a" ]
then
echo "Found 32 bit Arch "
mkArch32 >> $codename/BoardConfig.mk
elif [ $abi == "arm64-v8a" ]
then
echo "Found 64 bit Arch "
mkArch64 >> $codename/BoardConfig.mk
else
echo "Can't find arch using 32 bit"
mkArch32 >> $codename/BoardConfig.mk
fi

# Make Fstab

cd out
compressionType=$(file --mime-type recovery.img-ramdisk.* | cut -d / -f 2 | cut -d - -f 2)
if [ $compressionType == "lzma" ]
then
echo "Found lzma compression in ramdisk"
mv recovery.img-ramdisk.gz recovery.img-ramdisk.lzma
lzma -d recovery.img-ramdisk.lzma
elif [ $compressionType == "gzip" ]
then
echo "Found gzip compression in ramdisk"
gzip -d recovery.img-ramdisk.gz
elif [ $compressionType == "lz4" ]
then
echo "Found lz4 compression in ramdisk"
lz4 -d recovery.img-ramdisk.*
fi
cpio -idm < recovery.img-ramdisk
cd ..

copyRight()
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

EOF
}

getMounts()
{
for i in $(cat out/etc/recovery.fstab | grep -w $1 )
do
partition=$(echo $i | grep "/dev")
if [ ! -z "$partition" ]; then
echo $partition >> file
fi
done

if [[ $1 == "/boot" || $1 == "/recovery" ]]
then
echo $1 emmc $(cat file)
fi
if [[ $1 == "/system" || $1 == "/data" || $1 == "/cache" ]]
then
echo $1 ext4 $(cat file)
fi
rm file
}
copyRight >> $codename/recovery.fstab
getMounts /boot >> $codename/recovery.fstab
getMounts /recovery >> $codename/recovery.fstab
getMounts /system >> $codename/recovery.fstab
getMounts /data >> $codename/recovery.fstab
getMounts /cache >> $codename/recovery.fstab
echo "Twrp tree ready for $brand $codename"
#Clean 
rm -rf recovery.img mounts build.prop out/ 

