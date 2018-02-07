#!/bin/bash
tar -xvf $1
model=$(cat build.prop | grep "]:" | grep ro.product.model | tee)

if [ ! -z "$model" ]
then
sed 's/\[\([^]]*\)\]/\1/g' build.prop | sed 's/: /=/g' | tee > b.prop
rm build.prop
mv b.prop build.prop
rm $1
tar -czvf $1 build.prop recovery.img
echo "Making tree for non rooted device"
./mktree.sh $1
else
echo "Making tree for rooted device"
./mktree.sh $1
fi

