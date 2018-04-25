package com.github.twrpbuilder.mkTree;


import java.io.File;

import com.github.twrpbuilder.util.*;

import static com.github.twrpbuilder.MainActivity.otg;

public class MakeTree {
	private long l=0;
	private String compressionType;
	private boolean lz4,lzma;
	private ShellExecutor shell;
	private String copyRight=shell.CopyRight();
	private GetBuildInfo info;
	private Config config;
	private String out;
	private String type;
	private boolean mt;
	public static boolean AndroidImageKitchen;
	public static boolean landscape;
	private static String recoveryF=Config.recoveryFile;
	private boolean encrypted;
	public MakeTree(boolean mtk,String type){
		config=new Config();
		shell=new ShellExecutor();
	    out=config.outDir;
	    this.type=type;
	    this.mt=mtk;
		if(mtk)
		{
			extractKernel(true);
		}else
		{
			extractKernel(false);
		}	

		extractFstab();
		info=new GetBuildInfo();
    	if(!shell.mkdir(info.getPathS()))
    	{
    		System.out.println("Failed to make dir");
    		System.exit(0);
    	}
    			
    			if(info.getCodename().isEmpty())
		{
			shell.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' "+info.propFile()+" | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
			if(info.getCodename().isEmpty())
			{
				System.out.println("Failed to get info");
				new Clean();
				System.exit(0);
			}
		}
		BuildMakeFiles();
	}

	private void BuildMakeFiles(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Making omni_"+info.getCodename()+".mk");
				new FWriter("omni_"+info.getCodename()+".mk",getOmniData());
				System.out.println("Making Android.mk");
				new FWriter("Android.mk",getAndroidtData());
				System.out.println("Making AndroidProducts.mk");
				new FWriter("AndroidProducts.mk",getAndroidProductsData());
				System.out.println("Making kernel.mk");
				if(new File(out+recoveryF+"-zImage").exists())
				{
					shell.cp(out+recoveryF+"-zImage", info.getPathS()+"kernel");
				}
				if(new File(out+recoveryF+"-dt").length()!=l)
				{

					shell.cp(out+recoveryF+"-dt", info.getPathS()+"dt.img");
					new FWriter("kernel.mk",getKernelData(true));
				}else {
					new FWriter("kernel.mk",getKernelData(false));
				}
				MkFstab();

				if(mt)
				{
					MkBoardConfig(type);
				}else if(type.equals("mrvl") || type.equals("samsung"))
				{
					MkBoardConfig(type);
				}
				else
				{
					MkBoardConfig();
				}
				System.out.println((char)27 + "[31m" + "Warning :- Check recovery fstab before build"+(char)27 +"[0m");
				System.out.println("Build fingerPrint: "+info.getFingerPrint());
				System.out.println("tree ready for "+ info.getCodename());
				new Clean();
			}
		}).start();
	}

	private void extractKernel(boolean mtk) {
		shell.mkdir(out);
		if (AndroidImageKitchen)
		{
			System.out.println(shell.command("chmod 777 unpackimg.sh && ./unpackimg.sh "+recoveryF));
		}else {
			shell.command("chmod 777 umkbootimg");
			if(mtk)
			{
				shell.command("./umkbootimg "+recoveryF);
			}
			else
			{
				shell.command("./umkbootimg -i "+recoveryF+" -o "+out);
			}
		}
	}

	private String getKernelData(boolean dt) {
        String idata;
        idata=copyRight;
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := "+info.getPathS()+"kernel\n" +
				"BOARD_KERNEL_CMDLINE := "+cmdline()+"\n" +
				"BOARD_KERNEL_BASE := 0x"+readRamadiskData("base")+"\n" +
				"BOARD_KERNEL_PAGESIZE := "+readRamadiskData("pagesize")+"\n";
		if(dt) {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+readRamadiskData("ramdiskoff")+" --tags_offset 0x"+readRamadiskData("tagsoff")+" --dt device/"+info.getBrand()+File.separator+info.getCodename()+"/dt.img";
		}else {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+readRamadiskData("ramdiskoff")+" --tags_offset 0x"+readRamadiskData("tagsoff");
		}
		return idata;
	}

	private String readRamadiskData(String which){
		String thinew=shell.commandnoapp("cat "+out+recoveryF+"-"+which);
		return thinew;
	}

	private String cmdline(){
		String cm=shell.commandnoapp("cat "+out+recoveryF+"-cmdline");
		if (cm.contains("permissive"))
		{
			return cm;
		}else {
			return cm+" androidboot.selinux=permissive";
		}
	}

	public void extractFstab() {
		compressionType=shell.commandnoapp("cd "+out+" && file --mime-type "+recoveryF+"-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
		if(compressionType.contains("lzma"))
		{
			System.out.println("Found lzma comression in ramdisk");
			shell.command("mv "+out+recoveryF+"-ramdisk.gz "+out+recoveryF+"-ramdisk.lzma && lzma -d "+out+recoveryF+"-ramdisk.lzma  && cd "+out+" && cpio -i <"+recoveryF+"-ramdisk");
			lzma=true;
		}else if(compressionType.contains("gzip"))
		{
			System.out.println("Found gzip comression in ramdisk");
			shell.command("gzip -d "+out+recoveryF+"-ramdisk.gz && cd "+out+" && cpio -i <"+recoveryF+"-ramdisk");
		}
		else if(compressionType.contains("lz4"))
		{
			System.out.println("Found lz4 comression in ramdisk");
			shell.commandnoapp("cd "+out+" && lz4 -d "+recoveryF+"-ramdisk.*  "+recoveryF+"-ramdisk && cpio -i <"+recoveryF+"-ramdisk ");
			lz4=true;
		}
		else 
		{
			new Clean();
			System.out.println("failed to uncompress ramdisk");
			System.exit(0);
		}
		
	}

	private void FstablastMessage() {
		if(new File(out+"etc/twrp.fstab").exists()) {
			Fstab(out+"etc/twrp.fstab");
			shell.command("mkdir "+info.getPathS()+"stock && mv "+out+"etc/* "+info.getPathS()+"stock/");
		}else if (new File(out+"etc/recovery.fstab").exists())
		{
			Fstab(out+"etc/recovery.fstab");
			shell.command("mkdir "+info.getPathS()+"stock && mv "+out+"etc/* "+info.getPathS()+"stock/");
		}
		new FWriter(".travis.yml",generateTravis());
	}

	public void MkFstab() {
		System.out.println("Copying fstab");
		if(lz4==true || lzma==true)
		{
			CheckCompression();
		}
		FstablastMessage();
	}


	private boolean checkPartition(String path,String partition){
		String s=shell.commandnoapp("cat "+path+" | grep -iw "+partition);
		if (s.contains(partition))
		{
			return true;
		}else
		{
			return false;
		}
	}

	private void Fstab(String path)
	{
		/*use existing twrp fstab if exists*/
		if (path.contains("twrp.fstab"))
		{
			String toWrite = copyRight;
			toWrite+=shell.command("cat "+path);
			new FWriter("recovery.fstab", toWrite);
		}else {
			String toWrite = copyRight;
			if (checkPartition(path, "boot")) {
				toWrite += grepPartition(path, "boot");
			}

			if(checkPartition(path,"metadata"))
			{
				encrypted=true;
			}
			if (checkPartition(path, "data")) {
				if (checkPartition(path,"encrypt"))
				{
					encrypted=true;
					toWrite+=grepPartition(path,"data")+"flags=encryptable="+grepPartition(path,"encrypt")+"\n";
				}
				else
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
			if (otg) {
				toWrite += "/usb-otg auto /dev/block/sda1 flags=display=\"USB OTG\";storage;wipeingui;removable\n";
			}

			toWrite += "/external_sd vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SDcard\";storage;wipeingui;removable\n";
			new FWriter("recovery.fstab", toWrite);
		}
	}



	private String grepPartition(String path,String partition) {
		String fullpath=null;
		String s =shell.commandnoapp("for i in $(cat "+path+" | grep -wi /"+partition+")\n" +
				"do\n" +
				"a=$(echo $i | grep /dev)\n" +
				"echo $a\n" +
				"done");
		System.out.println(s);
		if(s.isEmpty())
		{
			s =shell.commandnoapp("for i in $(cat "+path+" | grep -wi /"+partition+")\n" +
					"do\n" +
					"a=$(echo $i | grep /emmc)\n" +
					"echo $a\n" +
					"done");
		}
		if (partition.equals("boot")|| partition.equals("recovery") || partition.equals(" fotakernel") || partition.equals("FOTAKernel") || partition.equals("misc"))
		{
			return  fullpath="/"+partition+" emmc "+s+"\n";
		}else if (partition.equals("system") || partition.equals("data") || partition.equals("cache"))
		{
			return fullpath="/"+partition+" ext4 "+s+"\n";
		}else {
			return null;
		}
	}
	
	private void CheckCompression() {
		String idata=null;
		if(lzma==true)
		{
		System.out.println("using lzma custom boot  ");
		idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/mkbootimg_lzma.mk";	
		}
		if(lz4==true)
		{
		System.out.println("using lz4 custom boot  ");
		idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/mkbootimg_lz4.mk";
		}
		if(idata!=null)
		{
			shell.command("echo "+idata +" >> " +info.getPath()+"/kernel.mk");
		}
	}

	private String getOmniData() {
		String idata =copyRight;
		idata+="$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)\n" + 
				"\n" + 
				"PRODUCT_COPY_FILES += "+info.getPathS()+"kernel:kernel\n" +
				"\n" + 
				"PRODUCT_DEVICE := "+ info.getCodename()+"\n" +
				"PRODUCT_NAME := omni_"+info.getCodename()+"\n" +
				"PRODUCT_BRAND := "+info.getBrand()+"\n" +
				"PRODUCT_MODEL := "+info.getModel()+"\n" +
				"PRODUCT_MANUFACTURER := "+info.getBrand();
		return idata;
	}
	

	private String getAndroidtData() {
		String idata =copyRight;
		idata+="ifneq ($(filter "+info.getCodename()+",$(TARGET_DEVICE)),)\n" +
				"\n" + 
				"LOCAL_PATH := "+info.getPath()+"\n" +
				"\n" + 
				"include $(call all-makefiles-under,$(LOCAL_PATH))\n" + 
				"\n" + 
				"endif";
		return idata;
	}

	private String getAndroidProductsData() {
		String idata =copyRight;
		idata+="LOCAL_PATH := "+info.getPath()+"\n" +
				"\n" + 
				"PRODUCT_MAKEFILES := $(LOCAL_PATH)/omni_"+info.getCodename()+".mk";
		return idata;
	}
	
	public void MkBoardConfig(){
		new FWriter("BoardConfig.mk",getBoardData("none"));

	}
	
	public void MkBoardConfig(String type){
		new FWriter("BoardConfig.mk",getBoardData(type));
	
	}

	private String generateTravis(){
	    String data="sudo: required\n" +
                "services:\n" +
                "  - docker\n" +
                "before_install:\n" +
                "  - docker pull yshalsager/cyanogenmod:latest\n" +
                "before_script:\n" +
                "- cd $HOME && mkdir twrp\n" +
                "- wget -q https://github.com/TwrpBuilder/twrp-sources/releases/download/omni_twrp-5.1.1-20180211/omni_twrp-5.1.1-20180211-norepo.tar.xz\n" +
                "  -O $HOME/twrp.tar.xz\n" +
                "- tar -xJf twrp.tar.xz --directory $HOME/twrp/ && rm twrp.tar.xz\n" +
                "script:\n" +
                "  - cd $HOME/twrp/ && git clone https://github.com/TwrpBuilder/android_device_"+info.getBrand()+"_"+info.getCodename()+".git device/"+info.getBrand()+File.separator+info.getCodename()+"\n" +
                "  - git clone https://github.com/TwrpBuilder/device_generic_twrpbuilder.git device/generic/twrpbuilder\n" +
                "  - rm -rf bootable/recovery && git clone https://github.com/omnirom/android_bootable_recovery.git bootable/recovery\n" +
                "  - |\n" +
                "    docker run --rm -i -e USER_ID=$(id -u) -e GROUP_ID=$(id -g) -v \"$(pwd):/home/cmbuild/twrp/:rw,z\" yshalsager/cyanogenmod bash << EOF\n" +
                "    cd /home/cmbuild/twrp/\n" +
                "    source build/envsetup.sh && lunch omni_"+info.getCodename()+"-eng && make -j16 recoveryimage\n" +
                "    exit\n" +
                "    EOF\n" +
                "after_success:\n" +
                "  - export version=$(cat bootable/recovery/variables.h | grep \"define TW_MAIN_VERSION_STR\" | cut -d '\"' -f2)\n" +
                "  - cp $HOME/twrp/out/target/product/"+info.getCodename()+File.separator+recoveryF+" $HOME/twrp/TWRP-$version-"+info.getCodename()+"-$(date +\"%Y%m%d\").img\n" +
                "\n" +
                "deploy:\n" +
                "  skip_cleanup: true\n" +
                "  provider: releases\n" +
                "  api_key: \"$GIT_OAUTH_TOKEN_TB\"\n" +
                "  file_glob: true\n" +
                "  file: $HOME/twrp/*.img\n" +
                "  on:\n" +
                "    tags: false\n" +
                "    repo: TwrpBuilder/android_device_"+info.getBrand()+"_"+info.getCodename()+"\n" +
                "    branch: master";
	    return data;
    }
	
	private String getBoardData(String type) {
		String idata =copyRight;
		idata+="LOCAL_PATH := "+info.getPath()+"\n" +
				"\n" + 
				"TARGET_BOARD_PLATFORM := "+info.getPlatform()+"\n" +
				"TARGET_BOOTLOADER_BOARD_NAME := "+info.getCodename()+"\n" +
				"\n" + 
				"# Recovery\n" + 
				"TARGET_USERIMAGES_USE_EXT4 := true\n" + 
				"BOARD_RECOVERYIMAGE_PARTITION_SIZE := "+info.getSize()+"\n" +
				"BOARD_FLASH_BLOCK_SIZE := 0\n" +
				"BOARD_HAS_NO_REAL_SDCARD := true\n" +
				"BOARD_HAS_NO_SELECT_BUTTON := true\n" +
				"BOARD_SUPPRESS_SECURE_ERASE := true\n" +
				"BOARD_HAS_NO_MISC_PARTITION := true\n" +
				"BOARD_RECOVERY_SWIPE := true\n" +
				"BOARD_USES_MMCUTILS := true\n" +
				"BOARD_SUPPRESS_EMMC_WIPE := true\n"
				+ "TW_EXCLUDE_SUPERSU := true\n"
				+ "TW_INPUT_BLACKLIST := \"hbtp_vm\"\n";
		if (landscape)
        {
            idata+="TW_THEME := landscape_hdpi\n";
        }
		if (type.equals("samsung"))
		{
			idata+="TW_HAS_DOWNLOAD_MODE := true\n" +
					"TW_NO_REBOOT_BOOTLOADER := true\n"+
					"BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/seEnforcing.mk\n";
		}
		if (encrypted)
		{
			idata+="TW_INCLUDE_CRYPTO := true\n";
		}

			System.out.println("found "+ info.getPlatform() + " platform" );
		/*Includes*/
		idata+="include $(LOCAL_PATH)/kernel.mk\n";
			if(type.equals("mrvl"))
			{
				idata+="include device/generic/twrpbuilder/mrvl.mk\n";
			}else if(type.equals("mt") || type.equals("mtk")){
				idata+="include device/generic/twrpbuilder/mtk.mk\n";
			}

		if(info.getApi().equals("armeabi-v7a"))
		{
			System.out.println("Found 32 bit arch");
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk\n";
		}else if(info.getApi().equals("arm64-v8a"))
		{
			System.out.println("Found 64 bit arch");
			idata+="include device/generic/twrpbuilder/BoardConfig64.mk\n";
		}else {
			System.out.println("no arch defined using 32 bit");
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk";
		}

		return idata;
	}
}
