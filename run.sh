#!/bin/bash
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
./mktree.sh $1
elif [ ! -z "$rooted" ]
then
echo "Making tree for rooted device"
./mktree.sh $1
else 
echo "UnSupported"
fi
