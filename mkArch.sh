#!/bin/bash
abi=$(cat build.prop | grep ro.product.cpu.abi= | cut -d = -f 2)
codename=$(cat build.prop | grep ro.build.product= | cut -d = -f 2)

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

#Clean 
rm -rf recovery.img mounts build.prop out/
