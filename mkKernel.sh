#!/bin/bash

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

./mkArch.sh
