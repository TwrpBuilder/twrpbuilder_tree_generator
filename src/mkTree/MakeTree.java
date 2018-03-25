package mkTree;


import java.io.File;

import util.*;

import javax.rmi.CORBA.Util;

public class MakeTree {
	private long l=0;
	private String compressionType;
	private boolean lz4,lzma;
	private String fstabIdata=ShellExecuter.CopyRight();
	public static boolean otg;
	private GetBuildInfo info;
	private Config config;
	private String out;
	public MakeTree(boolean mtk,String type){
	    out=config.outDir;
		if(mtk)
		{
			extractKernel(true);
		}else
		{
			extractKernel(false);
		}	

		extractFstab();
		info=new GetBuildInfo();
    	if(!ShellExecuter.mkdir(info.getPathS()))
    	{
    		System.out.println("Failed to make dir");
    		System.exit(0);
    	}
    			
    			if(info.getCodename().isEmpty())
		{
			ShellExecuter.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' "+info.propFile()+" | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
			if(info.getCodename().isEmpty())
			{
				System.out.println("Failed to get info");
				new Clean();
				System.exit(0);
			}
		}
		MkAndroid();
		MkAndroidProducts();
		MkOmni();
		if(mtk)
		{
		mkKernel(true);
		MkBoardConfig(type);
		}else if(type.equals("mrvl") || type.equals("samsung"))
		{
			mkKernel(false);
			MkBoardConfig(type);	
		}
		else
		{
			mkKernel(false);
			MkBoardConfig();
		}
		MkFstab();
	}
	

	private void extractKernel(boolean mtk) {
		ShellExecuter.command("chmod 777 umkbootimg");
		if(mtk)
		{
			ShellExecuter.command("./umkbootimg recovery.img");
		}
		else 
		{
		ShellExecuter.mkdir(out);
		ShellExecuter.command("./umkbootimg -i recovery.img -o "+out);
		}
	}

	private void mkKernel(boolean mtk) {
		System.out.println("Making kernel.mk");
		if(new File(out+"recovery.img-zImage").exists())
		{
		ShellExecuter.cp(out+"recovery.img-zImage", info.getPathS()+"kernel");
		}
		if(new File(out+"recovery.img-dt").length()!=l)
		{
		
			ShellExecuter.cp(out+"recovery.img-dt", info.getPathS()+"dt.img");
			new FWriter("kernel.mk",getKernelData(true));
		}else {
			new FWriter("kernel.mk",getKernelData(false));
		}

	}
	
	private String getKernelData(boolean dt) {
		String idata;
		String pagesize=ShellExecuter.commandnoapp("cat "+out+"recovery.img-pagesize");
		String cmdline=ShellExecuter.commandnoapp("cat "+out+"recovery.img-cmdline");
		String ramdiskofsset=ShellExecuter.commandnoapp("cat "+out+"recovery.img-ramdisk_offset");
		String tagsoffset=ShellExecuter.commandnoapp("cat "+out+"recovery.img-tags_offset");
		String kernelbase=ShellExecuter.commandnoapp("cat "+out+"recovery.img-base");
		idata=ShellExecuter.CopyRight();
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := "+info.getPathS()+"kernel\n" +
				"BOARD_KERNEL_CMDLINE := "+cmdline+" androidboot.selinux=permissive\n" + 
				"BOARD_KERNEL_BASE := 0x"+kernelbase+"\n" + 
				"BOARD_KERNEL_PAGESIZE := "+pagesize+"\n";
		if(dt) {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+ramdiskofsset+" --tags_offset 0x"+tagsoffset+" --dt device/"+info.getBrand()+File.separator+info.getCodename()+"/dt.img";
		}else {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+ramdiskofsset+" --tags_offset 0x"+tagsoffset;
		}
		return idata;
	}

	public void extractFstab() {
		compressionType=ShellExecuter.commandnoapp("cd "+out+" && file --mime-type recovery.img-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
		if(compressionType.equals("lzma"))
		{
			System.out.println("Found lzma comression in ramdisk");
			ShellExecuter.command("mv "+out+"recovery.img-ramdisk.gz "+out+"recovery.img-ramdisk.lzma && lzma -d "+out+"recovery.img-ramdisk.lzma  && cd "+out+" && cpio -i <recovery.img-ramdisk");
			lzma=true;
		}else if(compressionType.equals("gzip"))
		{
			System.out.println("Found gzip comression in ramdisk");
			ShellExecuter.command("gzip -d "+out+"recovery.img-ramdisk.gz && cd "+out+" && cpio -i <recovery.img-ramdisk");
		}
		else if(compressionType.equals("lz4"))
		{
			System.out.println("Found lz4 comression in ramdisk");
			ShellExecuter.commandnoapp("cd "+out+" && lz4 -d recovery.img-ramdisk.*  recovery.img-ramdisk && cpio -i <recovery.img-ramdisk ");
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
			ShellExecuter.command("mkdir "+info.getPathS()+"stock && mv "+out+"etc/* "+info.getPathS()+"stock/");
		}else if (new File(out+"etc/recovery.fstab").exists())
		{
			Fstab(out+"etc/recovery.fstab");
			ShellExecuter.command("mkdir "+info.getPathS()+"stock && mv "+out+"etc/* "+info.getPathS()+"stock/");
		}
		System.out.println("Build fingerPrint: "+info.getFingerPrint());
		System.out.println("tree ready for "+ info.getCodename());
		System.out.println((char)27 + "[31m" +"Waring :- Check recovery fstab before build");
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
		String s=ShellExecuter.commandnoapp("cat "+path+" | grep -iw "+partition);
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
		String toWrite=ShellExecuter.CopyRight();
		if (checkPartition(path,"boot"))
		{
			toWrite+=grepPartition(path,"boot");
		}
		if (checkPartition(path,"data"))
		{
			toWrite+=grepPartition(path,"data");
		}
		if (checkPartition(path,"system"))
		{
			toWrite+=grepPartition(path,"system");
		}
		if (checkPartition(path,"cache"))
		{
			toWrite+=grepPartition(path,"cache");
		}
		if (checkPartition(path,"fotakernel"))
		{
			toWrite+=grepPartition(path,"fotakernel");
		}
		if (checkPartition(path,"FOTAKernel"))
		{
			toWrite+=grepPartition(path,"FOTAKernel");
		}
		if (checkPartition(path,"recovery"))
		{
			toWrite+=grepPartition(path,"recovery");
		}
		if (otg)
		{
			toWrite+="/usb-otg auto /dev/block/sda1 flags=display=\"USB OTG\";storage;wipeingui;removable\n";
		}
		toWrite+="/external_sd vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SDcard\";storage;wipeingui;removable\n";
		new FWriter("recovery.fstab",toWrite);
	}



	private String grepPartition(String path,String partition) {
		String fullpath=null;
		String s =ShellExecuter.commandnoapp("for i in $(cat "+path+" | grep -wi /"+partition+")\n" +
				"do\n" +
				"a=$(echo $i | grep /dev)\n" +
				"echo $a\n" +
				"done");
		System.out.println(s);

		if(s.isEmpty())
		{
			s =ShellExecuter.commandnoapp("for i in $(cat "+path+" | grep -wi /"+partition+")\n" +
					"do\n" +
					"a=$(echo $i | grep /emmc)\n" +
					"echo $a\n" +
					"done");
		}
		if (partition.equals("boot")|| partition.equals("recovery") || partition.equals(" fotakernel") || partition.equals("FOTAKernel"))
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
			ShellExecuter.command("echo "+idata +" >> " +info.getPath()+"/kernel.mk");
		}
	}
	
	public void MkOmni() {
		System.out.println("Making omni_"+info.getCodename()+".mk");
		new FWriter("omni_"+info.getCodename()+".mk",getOmniData());
	}
	
	private String getOmniData() {
		String idata =ShellExecuter.CopyRight();
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
	
	
	public void MkAndroid() {
		System.out.println("Making Android.mk");
		new FWriter("Android.mk",getAndroidtData());

	}
	
	private String getAndroidtData() {
		String idata =ShellExecuter.CopyRight();
		idata+="ifneq ($(filter "+info.getCodename()+",$(TARGET_DEVICE)),)\n" +
				"\n" + 
				"LOCAL_PATH := "+info.getPath()+"\n" +
				"\n" + 
				"include $(call all-makefiles-under,$(LOCAL_PATH))\n" + 
				"\n" + 
				"endif";
		return idata;
	}

	public void MkAndroidProducts() {
		System.out.println("Making AndroidProducts.mk");
		new FWriter("AndroidProducts.mk",getAndroidProductsData());

	}
	
	private String getAndroidProductsData() {
		String idata =ShellExecuter.CopyRight();
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
	
	private String getBoardData(String type) {
		String idata =ShellExecuter.CopyRight();
		idata+="LOCAL_PATH := "+info.getPath()+"\n" +
				"\n" + 
				"TARGET_BOARD_PLATFORM := "+info.getPlatform()+"\n" +
				"TARGET_BOOTLOADER_BOARD_NAME := "+info.getCodename()+"\n" +
				"\n" + 
				"# Recovery\n" + 
				"TARGET_USERIMAGES_USE_EXT4 := true\n" + 
				"BOARD_RECOVERYIMAGE_PARTITION_SIZE := "+info.getSize()+" \n" +
				"BOARD_FLASH_BLOCK_SIZE := 1000000\n" + 
				"BOARD_HAS_NO_REAL_SDCARD := true\n" + 
				"TW_EXCLUDE_SUPERSU := true\n"
				+ "TW_INPUT_BLACKLIST := \"hbtp_vm\"\n"
				+ "include $(LOCAL_PATH)/kernel.mk\n";
	
			System.out.println("found "+ info.getPlatform() + " platform" );
			if(type.equals("samsung"))
			{
				idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/seEnforcing.mk\n";	
			}
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
