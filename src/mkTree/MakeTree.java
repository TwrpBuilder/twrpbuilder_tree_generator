package mkTree;


import java.io.File;

import util.Clean;
import util.FWriter;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MakeTree {
	private long l=0;
	private String compressionType;
	private boolean lz4,lzma;
	private String fstabIdata=ShellExecuter.CopyRight();
	public static boolean otg;

	
	public MakeTree(boolean mtk,String type){
	
		if(mtk)
		{
			extractKernel(true);
		}else
		{
			extractKernel(false);
		}	

		extractFstab();
		new GetBuildInfo();
    	if(!ShellExecuter.mkdir(GetBuildInfo.getPathS()))
    	{
    		System.out.println("Failed to make dir");
    		System.exit(0);
    	}
    			
    			if(GetBuildInfo.getCodename().isEmpty())
		{
			ShellExecuter.command("sed 's/\\[\\([^]]*\\)\\]/\\1/g' "+GetBuildInfo.propFile()+" | sed 's/: /=/g' | tee > b.prop && mv -f b.prop build.prop");
			if(GetBuildInfo.getCodename().isEmpty())
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
		ShellExecuter.mkdir("out");
		ShellExecuter.command("./umkbootimg -i recovery.img -o out/ ");
		}
	}

	private void mkKernel(boolean mtk) {
		System.out.println("Making kernel.mk");
		if(new File("out/recovery.img-zImage").exists())
		{
		ShellExecuter.cp("out/recovery.img-zImage", GetBuildInfo.getPathS()+"kernel");
		}
		if(new File("out/recovery.img-dt").length()!=l)
		{
		
			ShellExecuter.cp("out/recovery.img-dt", GetBuildInfo.getPathS()+"dt.img");
			new FWriter("kernel.mk",getKernelData(true));
		}else {
			new FWriter("kernel.mk",getKernelData(false));
		}

	}
	
	private String getKernelData(boolean dt) {
		String idata;
		String pagesize=ShellExecuter.commandnoapp("cat out/recovery.img-pagesize");
		String cmdline=ShellExecuter.commandnoapp("cat out/recovery.img-cmdline");
		String ramdiskofsset=ShellExecuter.commandnoapp("cat out/recovery.img-ramdisk_offset");
		String tagsoffset=ShellExecuter.commandnoapp("cat out/recovery.img-tags_offset");
		String kernelbase=ShellExecuter.commandnoapp("cat out/recovery.img-base");
		idata=ShellExecuter.CopyRight();
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := "+GetBuildInfo.getPathS()+"kernel\n" + 
				"BOARD_KERNEL_CMDLINE := "+cmdline+" androidboot.selinux=permissive\n" + 
				"BOARD_KERNEL_BASE := 0x"+kernelbase+"\n" + 
				"BOARD_KERNEL_PAGESIZE := "+pagesize+"\n";
		if(dt) {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+ramdiskofsset+" --tags_offset 0x"+tagsoffset+" --dt device/"+GetBuildInfo.getBrand()+File.separator+GetBuildInfo.getCodename()+"/dt.img";
		}else {
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+ramdiskofsset+" --tags_offset 0x"+tagsoffset;
		}
		return idata;
	}

	private void Fstab(String path)
	{
		makeFstab(grepPartition(path,"boot"));
		makeFstab(grepPartition(path,"recovery"));
		makeFstab(grepPartition(path,"system"));
		makeFstab(grepPartition(path,"data"));
		makeFstab(grepPartition(path,"cache"));
		makeFstab(grepPartition(path,"fotakernel"));
		makeFstab("/dev/block/mmcblk1p1");
		if(otg)
		{
		makeFstab("/dev/block/sda1");
		}
	}
	
	public void extractFstab() {
		compressionType=ShellExecuter.commandnoapp("cd out && file --mime-type recovery.img-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
		if(compressionType.equals("lzma"))
		{
			System.out.println("Found lzma comression in ramdisk");
			ShellExecuter.command("mv out/recovery.img-ramdisk.gz out/recovery.img-ramdisk.lzma && lzma -d out/recovery.img-ramdisk.lzma  && cd out && cpio -i <recovery.img-ramdisk");
			lzma=true;
		}else if(compressionType.equals("gzip"))
		{
			System.out.println("Found gzip comression in ramdisk");
			ShellExecuter.command("gzip -d out/recovery.img-ramdisk.gz && cd out && cpio -i <recovery.img-ramdisk");
		}
		else if(compressionType.equals("lz4"))
		{
			System.out.println("Found lz4 comression in ramdisk");
			ShellExecuter.commandnoapp("cd out && lz4 -d recovery.img-ramdisk.*  recovery.img-ramdisk && cpio -i <recovery.img-ramdisk ");
			lz4=true;
		}
		else 
		{
			new Clean();
			System.out.println("failed to uncompress ramdisk");
			System.exit(0);
		}
		
	}
	
	public void MkFstab() {
		System.out.println("Copying fstab");
		FstablastMessage();
		if(lz4==true || lzma==true)
		{
			CheckCompression();	
		}
	}
	
	
	
	private void FstablastMessage() {
		if(new File("out/etc").exists()) {
		Fstab("out/etc/recovery.fstab");
		ShellExecuter.command("mkdir "+GetBuildInfo.getPathS()+"stock && mv out/etc/* "+GetBuildInfo.getPathS()+"stock/");
		}
		System.out.println("Build fingerPrint: "+GetBuildInfo.getFingerPrint());
		System.out.println("tree ready for "+ GetBuildInfo.getCodename());
		System.out.println((char)27 + "[31m" +"Waring :- Check recovery fstab before build");
	}
	
	
	private void makeFstab(String pPath) {
		if(pPath.endsWith("boot") || pPath.endsWith("BOOT") || pPath.endsWith("Boot"))
		{
			fstabIdata+="/boot emmc "+pPath+"\n";
		}
		
		if(pPath.endsWith("recovery") || pPath.endsWith("RECOVERY") || pPath.endsWith("Recovery"))
		{
			fstabIdata+="/recovery emmc "+pPath+"\n";
		}
		
		if(pPath.endsWith("system") || pPath.endsWith("SYSTEM") || pPath.endsWith("System") || pPath.endsWith("emmc@android"))
		{
			fstabIdata+="/system ext4 "+pPath+"\n";
		}
		
		if(pPath.contains("data") || pPath.contains("DATA") || pPath.contains("Data"))
		{
			fstabIdata+="/data ext4 "+pPath+"\n";
		}
		
		if(pPath.endsWith("cache") || pPath.endsWith("CACHE") || pPath.endsWith("Cache"))
		{
			fstabIdata+="/cache ext4 "+pPath+"\n";
		}
		
		if(pPath.endsWith("mmcblk1p1"))
		{
			fstabIdata+="/external_sd vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SDcard\";storage;wipeingui;removable\n";
		}
		
		if(pPath.endsWith("sda1"))
		{
			fstabIdata+="/usb-otg auto /dev/block/sda1	flags=display=\"USB OTG\";storage;wipeingui;removable\n";
		}
		
		if(pPath.endsWith("FOTAKernel") || pPath.endsWith("fotakernel"))
		{
			fstabIdata+="/recovery ext4 "+pPath+"\n";	
		}
		
		new FWriter("recovery.fstab",fstabIdata);
	}
	
	private String grepPartition(String path,String partition) {
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
	return s;
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
			ShellExecuter.command("echo "+idata +" >> " +GetBuildInfo.getPath()+"/kernel.mk");
		}
	}
	
	public void MkOmni() {
		System.out.println("Making omni_"+GetBuildInfo.getCodename()+".mk");
		new FWriter("omni_"+GetBuildInfo.getCodename()+".mk",getOmniData());
	}
	
	private String getOmniData() {
		String idata =ShellExecuter.CopyRight();
		idata+="$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)\n" + 
				"\n" + 
				"PRODUCT_COPY_FILES += "+GetBuildInfo.getPathS()+"kernel:kernel\n" + 
				"\n" + 
				"PRODUCT_DEVICE := "+ GetBuildInfo.getCodename()+"\n" + 
				"PRODUCT_NAME := omni_"+GetBuildInfo.getCodename()+"\n" + 
				"PRODUCT_BRAND := "+GetBuildInfo.getBrand()+"\n" + 
				"PRODUCT_MODEL := "+GetBuildInfo.getModel()+"\n" + 
				"PRODUCT_MANUFACTURER := "+GetBuildInfo.getBrand();
		return idata;
	}
	
	
	public void MkAndroid() {
		System.out.println("Making Android.mk");
		new FWriter("Android.mk",getAndroidtData());

	}
	
	private String getAndroidtData() {
		String idata =ShellExecuter.CopyRight();
		idata+="ifneq ($(filter "+GetBuildInfo.getCodename()+",$(TARGET_DEVICE)),)\n" + 
				"\n" + 
				"LOCAL_PATH := "+GetBuildInfo.getPath()+"\n" + 
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
		idata+="LOCAL_PATH := "+GetBuildInfo.getPath()+"\n" + 
				"\n" + 
				"PRODUCT_MAKEFILES := $(LOCAL_PATH)/omni_"+GetBuildInfo.getCodename()+".mk";
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
		idata+="LOCAL_PATH := "+GetBuildInfo.getPath()+"\n" + 
				"\n" + 
				"TARGET_BOARD_PLATFORM := "+GetBuildInfo.getPlatform()+"\n" + 
				"TARGET_BOOTLOADER_BOARD_NAME := "+GetBuildInfo.getCodename()+"\n" + 
				"\n" + 
				"# Recovery\n" + 
				"TARGET_USERIMAGES_USE_EXT4 := true\n" + 
				"BOARD_RECOVERYIMAGE_PARTITION_SIZE := "+GetBuildInfo.getSize()+" \n" + 
				"BOARD_FLASH_BLOCK_SIZE := 1000000\n" + 
				"BOARD_HAS_NO_REAL_SDCARD := true\n" + 
				"TW_EXCLUDE_SUPERSU := true\n"
				+ "TW_INPUT_BLACKLIST := \"hbtp_vm\"\n"
				+ "include $(LOCAL_PATH)/kernel.mk\n";
	
			System.out.println("found "+ GetBuildInfo.getPlatform() + " platform" );
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

		if(GetBuildInfo.getApi().equals("armeabi-v7a"))
		{
			System.out.println("Found 32 bit arch");
			idata+="include device/generic/twrpbuilder/BoardConfig32.mk\n";
		}else if(GetBuildInfo.getApi().equals("arm64-v8a"))
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
