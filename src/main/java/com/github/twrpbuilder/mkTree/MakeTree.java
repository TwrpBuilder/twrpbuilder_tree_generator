package com.github.twrpbuilder.mkTree;


import com.github.twrpbuilder.Interface.OnDataRequest;
import com.github.twrpbuilder.Interface.Tools;
import com.github.twrpbuilder.Models.DeviceModel;
import com.github.twrpbuilder.Models.OptionsModel;
import com.github.twrpbuilder.util.Config;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;


public class MakeTree extends Tools {
    public boolean landscape;
    private String recoveryF = Config.recoveryFile;
    private long l = 0;
    private String compressionType;
    private boolean lz4, lzma;
    private String copyRight = CopyRight();
    private Config config;
    private String out;
    private DeviceModel deviceModel;
    private OptionsModel optionsModel;

    public MakeTree(DeviceModel d, OptionsModel optionsModel) {
        this.deviceModel=d;
        this.optionsModel=optionsModel;
        config = new Config();
        out = config.outDir;
        extractKernel();
        extractFstab();

        //NOTE:// save var in strings instead of reading again
        PropData(new OnDataRequest() {
            @Override
            public void getData(DeviceModel data) {
                data.setType(deviceModel.getType());
                data.setMtk(deviceModel.isMtk());
                data.setSamsung(deviceModel.isSamsung());
                data.setMrvl(deviceModel.isMrvl());
                deviceModel=data;
                if (!mkdir(deviceModel.getPath())) {
                    System.out.println("Failed to make dir");
                    System.exit(-1);
                }
                if (deviceModel.getCodename().isEmpty()) {
                    command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' " + propFile() + " | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
                    if (deviceModel.getCodename().isEmpty()) {
                        System.out.println("Failed to get codeName");
                        Clean();
                        System.exit(-1);
                    }
                }
                if (new File(deviceModel.getPath()+"kernel.mk").exists())
                {
                    System.out.println("Do you want to overwrite "+deviceModel.getPath()+" ?( default: n)");
                    Scanner read=new Scanner(System.in);
                    String in=read.nextLine();
                    if (!in.isEmpty() && in.equals("y"))
                        BuildMakeFiles();
                    else {
                        warnings();
                        Clean();
                    }
                }
                else
                    BuildMakeFiles();
            }
        });
    }

    private void BuildMakeFiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Making omni_" + deviceModel.getCodename() + ".mk");
                Write(deviceModel.getPath()+"omni_" + deviceModel.getCodename() + ".mk", getOmniData());
                System.out.println("Making Android.mk");
                Write(deviceModel.getPath()+"Android.mk", getAndroidtData());
                System.out.println("Making AndroidProducts.mk");
                Write(deviceModel.getPath()+"AndroidProducts.mk", getAndroidProductsData());
                System.out.println("Making kernel.mk");
                if (fexist(out + recoveryF + "-zImage")) {
                    cp(out + recoveryF + "-zImage", deviceModel.getPath() + "kernel");
                }
                if (new File(out + recoveryF + "-dt").length() != l) {

                    cp(out + recoveryF + "-dt", deviceModel.getPath() + "dt.img");
                    Write(deviceModel.getPath()+"kernel.mk", getKernelData(true));
                } else {
                    Write(deviceModel.getPath()+"kernel.mk", getKernelData(false));
                }
                MkFstab();
                MkBoardConfig();
                System.out.println("Build fingerPrint: " + deviceModel.getFingerprint());
                warnings();
                Clean();
            }
        }).start();
    }

    private void warnings(){
        System.out.println("tree ready for " + deviceModel.getCodename() +" at device"+seprator+deviceModel.getBrand()+seprator+deviceModel.getCodename());
        System.out.println((char) 27 + "[31m" + "Warning :- Check recovery fstab before build" + (char) 27 + "[0m");
    }

    private void extractKernel() {
        mkdir(out);
        if (optionsModel.isAndroidImageKitchen()) {
            System.out.println(command("chmod 777 unpackimg.sh && ./unpackimg.sh " + recoveryF));
        } else {
            command("chmod 777 umkbootimg");
            if (deviceModel.isMtk()) {
                command("./umkbootimg " + recoveryF);
            } else {
                command("./umkbootimg -i " + recoveryF + " -o " + out);
            }
        }
    }

    private String getKernelData(boolean dt) {
        String idata;
        idata = copyRight;
        idata += "# Kernel\n" +
                "TARGET_PREBUILT_KERNEL := $(LOCAL_PATH)/kernel\n" +
                "BOARD_KERNEL_CMDLINE := " + cmdline() + "\n" +
                "BOARD_KERNEL_BASE := 0x" + readRamadiskData("base") + "\n" +
                "BOARD_KERNEL_PAGESIZE := " + readRamadiskData("pagesize") + "\n";
        if (dt) {
            idata += "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x" + readRamadiskData("ramdiskoff") + " --tags_offset 0x" + readRamadiskData("tagsoff") + " --dt $(LOCAL_PATH)/dt.img";
        } else {
            idata += "BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x" + readRamadiskData("ramdiskoff") + " --tags_offset 0x" + readRamadiskData("tagsoff");
        }
        return idata;
    }

    private String readRamadiskData(String which) {
        String thinew = command("cat " + out + recoveryF + "-" + which);
        return thinew;
    }

    private String cmdline() {
        String cm = command("cat " + out + recoveryF + "-cmdline");
        if (cm.contains("permissive")) {
            return cm;
        } else {
            return cm + " androidboot.selinux=permissive";
        }
    }

    public void extractFstab() {
        compressionType = command("cd " + out + " && file --mime-type " + recoveryF + "-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
        if (compressionType.contains("lzma")) {
            System.out.println("Found lzma comression in ramdisk");
            command("mv " + out + recoveryF + "-ramdisk.gz " + out + recoveryF + "-ramdisk.lzma && lzma -d " + out + recoveryF + "-ramdisk.lzma  && cd " + out + " && cpio -i <" + recoveryF + "-ramdisk");
            lzma = true;
        } else if (compressionType.contains("gzip")) {
            System.out.println("Found gzip comression in ramdisk");
            command("gzip -d " + out + recoveryF + "-ramdisk.gz && cd " + out + " && cpio -i <" + recoveryF + "-ramdisk");
        } else if (compressionType.contains("lz4")) {
            System.out.println("Found lz4 comression in ramdisk");
            command("cd " + out + " && lz4 -d " + recoveryF + "-ramdisk.*  " + recoveryF + "-ramdisk && cpio -i <" + recoveryF + "-ramdisk ");
            lz4 = true;
        } else {
            Clean();
            System.out.println("failed to uncompress ramdisk");
            System.exit(-1);
        }

    }

    private void FstablastMessage() {
        if (fexist(out + "etc/twrp.fstab")) {
            System.out.println("Copying fstab");
            Fstab(out + "etc/twrp.fstab");
            command("mkdir " + deviceModel.getPath() + "stock && mv " + out + "etc/* " + deviceModel.getPath() + "stock/");
        } else if (fexist(out + "etc/recovery.fstab")) {
            System.out.println("Generating fstab");
            Fstab(out + "etc/recovery.fstab");
            command("mkdir " + deviceModel.getPath() + "stock && mv " + out + "etc/* " + deviceModel.getPath() + "stock/");
        }
    }

    public void MkFstab() {
        if (lz4 == true || lzma == true) {
            CheckCompression();
        }
        FstablastMessage();
    }


    private boolean checkPartition(String path, String partition) {
        String s = command("cat " + path + " | grep -iw " + partition);
        if (s.contains(partition)) {
            return true;
        } else {
            return false;
        }
    }

    private void Fstab(String path) {
        /*use existing twrp fstab if exists*/
        if (path.contains("twrp.fstab")) {
            String toWrite = copyRight;
            toWrite += command("cat " + path);
            Write("recovery.fstab", toWrite);
        } else {
            String toWrite = copyRight;
            if (checkPartition(path, "boot")) {
                toWrite += grepPartition(path, "boot");
            }

            if (checkPartition(path, "metadata") || checkPartition(path,"encrypt")) {
                deviceModel.setEncrypted(true);
            }
            if (checkPartition(path, "data")) {
                toWrite += grepPartition(path, "data");
            }
            if (checkPartition(path, "system")) {
                toWrite += grepPartition(path, "system");
            }
            if (checkPartition(path, "cache")) {
                toWrite += grepPartition(path, "cache");
            }
            if (checkPartition(path, "misc")) {
                toWrite += grepPartition(path, "misc");
            }
            if (checkPartition(path, "fotakernel")) {
                toWrite += grepPartition(path, "fotakernel");
            }
            if (checkPartition(path, "FOTAKernel")) {
                toWrite += grepPartition(path, "FOTAKernel");
            }
            if (checkPartition(path, "recovery")) {
                toWrite += grepPartition(path, "recovery");
            }
            if (optionsModel.isOtg()) {
                toWrite += "/usb-otg auto /dev/block/sda1 /dev/block/sda flags=display=\"USB OTG\";storage;wipeingui;removable\n";
            }

            toWrite += "/external_sd vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SDcard\";storage;wipeingui;removable\n";
            Write("recovery.fstab", toWrite);
        }
    }


    private String grepPartition(String path, String partition) {
        String fullpath = null;
        String tmp="";

        LinkedList<String> s = command("for i in $(cat " + path + " | grep -wi /" + partition + ")\n" +
                "do\n" +
                "echo $i | grep /dev\n" +
                "done",true);
        if (s.isEmpty()) {
            s = command("for i in $(cat " + path + " | grep -wi /" + partition + ")\n" +
                    "do\n" +
                    "echo $i | grep /emmc\n" +
                    "done",true);
        }

        ListIterator<String> listIterator=s.listIterator();
        while (listIterator.hasNext())
        {
            String o=listIterator.next();
            if (!o.isEmpty())
            {
                if (
                        partition.equals("boot") ||
                                partition.equals("recovery") ||
                                partition.equals("fotakernel") ||
                                partition.equals("FOTAKernel") ||
                                partition.equals("misc")
                        ) {

                    fullpath = "/" + partition + " emmc " + o + "\n";
                } else if (partition.equals("system") ||
                        partition.equals("data") ||
                        partition.contains("metadata") ||
                        partition.equals("cache")) {
                    if (partition.equals("data") && o.contains("metadata"))
                    {
                        if (o.contains("/metadata"))
                        {
                            tmp += o
                                    .replace("wait,check,resize,"," flags=")
                                    .replace(",","")
                                    .replace("forceencrypt","encryptable")+ "\n";

                        }
                        else {
                            tmp += "/" + partition + " ext4 " + o
                                    .replace("wait,check,resize,", " ")
                                    .replace(",", "");
                        }
                        fullpath+=tmp;

                    }
                    else
                    fullpath = "/" + partition + " ext4 " + o
                            .replace("wait,check,resize,"," ")
                            .replace(",","") + "\n";
                }
                else
                {
                    fullpath=null;
                }
                System.out.println(fullpath.trim());
            }

        }

        if (fullpath!=null)
        {
            return fullpath;
        }
        else {
            return null;
        }

    }

    private void CheckCompression() {
        String idata = "";
        if (lzma == true) {
            System.out.println("using lzma custom boot  ");
            idata += "BOARD_NEEDS_LZMA_MINIGZIP := true";
        }
        if (lz4 == true) {
            System.out.println("using lz4 custom boot  ");
            idata += "BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/mkbootimg_lz4.mk";
        }
        if (idata != null) {
            command("echo " + idata + " >> " + deviceModel.getPath() + "/kernel.mk");
        }
    }

    private String getOmniData() {
        String idata = copyRight;
        idata += "$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)\n" +
                "PRODUCT_DEVICE := " + deviceModel.getCodename() + "\n" +
                "PRODUCT_NAME := omni_" + deviceModel.getCodename() + "\n" +
                "PRODUCT_BRAND := " + deviceModel.getBrand() + "\n" +
                "PRODUCT_MODEL := " + deviceModel.getModel() + "\n" +
                "PRODUCT_MANUFACTURER := " + deviceModel.getBrand();
        return idata;
    }


    private String getAndroidtData() {
        String idata = copyRight;
        idata += "ifneq ($(filter " + deviceModel.getCodename() + ",$(TARGET_DEVICE)),)\n" +
                "\n" +
                "LOCAL_PATH := " + deviceModel.getPath() + "\n" +
                "\n" +
                "include $(call all-makefiles-under,$(LOCAL_PATH))\n" +
                "\n" +
                "endif";
        return idata;
    }

    private String getAndroidProductsData() {
        String idata = copyRight;
        idata += "LOCAL_PATH := " + deviceModel.getPath() + "\n" +
                "\n" +
                "PRODUCT_MAKEFILES := $(LOCAL_PATH)/omni_" + deviceModel.getCodename() + ".mk";
        return idata;
    }

    public void MkBoardConfig() {
        Write(deviceModel.getPath()+"BoardConfig.mk", getBoardData());

    }

    private String getBoardData() {
        String idata = copyRight;
        idata += "LOCAL_PATH := " + deviceModel.getPath().substring(0,deviceModel.getPath().length()-1) + "\n" +
                "\n" +
                "TARGET_BOARD_PLATFORM := " + deviceModel.getPlatform() + "\n" +
                "TARGET_BOOTLOADER_BOARD_NAME := " + deviceModel.getCodename() + "\n" +
                "\n" +
                "# Recovery\n" +
                "TARGET_USERIMAGES_USE_EXT4 := true\n" +
                "BOARD_RECOVERYIMAGE_PARTITION_SIZE := " + getSize() + "\n" +
                "BOARD_FLASH_BLOCK_SIZE := 0\n" +
                "BOARD_HAS_NO_REAL_SDCARD := true\n" +
                "BOARD_SUPPRESS_SECURE_ERASE := true\n" +
                "BOARD_HAS_NO_MISC_PARTITION := true\n" +
                "BOARD_RECOVERY_SWIPE := true\n" +
                "BOARD_USES_MMCUTILS := true\n" +
                "BOARD_SUPPRESS_EMMC_WIPE := true\n"
                + "TW_INPUT_BLACKLIST := \"hbtp_vm\"\n";
        if (landscape) {
            idata += "TW_THEME := landscape_hdpi\n";
        }
        if (deviceModel.isSamsung()) {
            idata += "TW_HAS_DOWNLOAD_MODE := true\n" +
                    "TW_NO_REBOOT_BOOTLOADER := true\n" +
                    "BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/seEnforcing.mk\n";
        }
        if (deviceModel.isEncrypted()) {
            idata += "TW_INCLUDE_CRYPTO := true\n";
        }

        System.out.println("found " + deviceModel.getPlatform() + " platform");
        /*Includes*/
        idata += "include $(LOCAL_PATH)/kernel.mk\n";
        if (deviceModel.isMrvl()) {
            idata += "include device/generic/twrpbuilder/mrvl.mk\n";
        } else if (deviceModel.isMtk() ||deviceModel.isMtk()) {
            idata += "include device/generic/twrpbuilder/mtk.mk\n";
        }

        if (deviceModel.getAbi().equals("armeabi-v7a")) {
            System.out.println("Found 32 bit arch");
            idata += "include device/generic/twrpbuilder/BoardConfig32.mk\n";
        } else if (deviceModel.getAbi().equals("arm64-v8a")) {
            System.out.println("Found 64 bit arch");
            idata += "include device/generic/twrpbuilder/BoardConfig64.mk\n";
        } else {
            System.out.println("no arch defined using 32 bit");
            idata += "include device/generic/twrpbuilder/BoardConfig32.mk";
        }

        return idata;
    }
}
