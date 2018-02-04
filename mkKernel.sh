#!/bin/bash
brand=$(cat build.prop | grep ro.product.brand= | cut -d = -f 2)
codename=$(cat build.prop | grep ro.build.product= | cut -d = -f 2)
mkdir out
./umkbootimg -i recovery.img -o out/ &> out/output.txt
cd out
checkdtSize=$(cat output.txt | grep BOARD_DT_SIZE | cut -d ">" -f 2)
pagesize=$(cat recovery.img-pagesize)
cmdline=$(cat recovery.img-cmdline)
ramdiskofsset=$(cat recovery.img-ramdisk_offset)
kernelbase=$(cat recovery.img-base)
cd ..

mkdt()
{
if [ $checkdtSize == 0 ]
then
echo "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset $ramdiskofsset"
else
echo "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset $ramdiskofsset --dt device/$brand/$codename/dt.img"
cp out/recovery.img-dt $codename/dt.img
fi
}

mkKernelInfo(){
cat << EOF

# Kernel
TARGET_PREBUILT_KERNEL := device/$brand/$codename/kernel
BOARD_KERNEL_CMDLINE := $cmdline androidboot.selinux=permissive
BOARD_KERNEL_BASE := $kernelbase
BOARD_KERNEL_PAGESIZE := $pagesize
EOF
mkdt
}

mkKernelInfo >> $codename/BoardConfig.mk
cp out/recovery.img-zImage $codename/kernel

./mkArch.sh
