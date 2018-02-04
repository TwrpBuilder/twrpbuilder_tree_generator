#!/bin/bash

fetchMounts(){
for i in $(cat mounts | grep /dev/block/mmcblk | grep -w $1 )
do
partition=$(echo $i | grep mmcblk0p)
if [ ! -z "$partition" ]; then
echo $partition >> file
fi
done

if [[ $1 == "/system" || $1 == "/data" || $1 == "/cache" ]]
then
echo $1 ext4 $(cat file)
fi
rm file
}

fetchMounts /system >> $codename/recovery.fstab
fetchMounts /data >> $codename/recovery.fstab
fetchMounts /cache >> $codename/recovery.fstab

#Clean 
rm -rf recovery.img mounts build.prop out/
