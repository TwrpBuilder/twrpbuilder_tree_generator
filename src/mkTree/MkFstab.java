package mkTree;

import util.Clean;
import util.FWriter;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MkFstab {
	private String compressionType;
	private boolean lz4,lzma;
	public MkFstab() {
		System.out.println("Making fstab");
		compressionType=ShellExecuter.commandnoapp("cd out && file --mime-type recovery.img-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
		if(compressionType.equals("lzma"))
		{
			System.out.println("Found lzma comression in ramdisk");
			ShellExecuter.command("mv out/recovery.img-ramdisk.gz out/recovery.img-ramdisk.lzma && lzma -d out/recovery.img-ramdisk.lzma && ls out/");
			new FWriter("recovery.fstab",getMounts());
			lzma=true;
			CheckCompression();
		}else if(compressionType.equals("gzip"))
		{
			System.out.println("Found gzip comression in ramdisk");
			ShellExecuter.command("gzip -d out/recovery.img-ramdisk.gz");
			new FWriter("recovery.fstab",getMounts());
		}else if(compressionType.equals("lz4"))
		{
			System.out.println("Found lz4 comression in ramdisk");
		ShellExecuter.command("lz4 -d out/recovery.img-ramdisk.*");
		lz4=true;
		CheckCompression();
		new FWriter("recovery.fstab",getMounts());
		}else if(GetBuildInfo.mtk==true)
		{
			new FWriter("recovery.fstab",getMountsMtk());
		}else {
			System.out.println("Failed to decompress ramdisk ");
			new Clean();
			System.exit(1);
		}
		
	}
	
	private String getMounts() {
		ShellExecuter.command("cd out && cpio -idm < recovery.img-ramdisk");
		String system=ShellExecuter.command("cat out/etc/recovery.fstab | grep -w '/system' | grep /dev | awk '{ print $1 }'");
		String data=ShellExecuter.command("cat out/etc/recovery.fstab | grep -w '/data' | grep /dev | awk '{ print $1 }'");
		String cache=ShellExecuter.command("cat out/etc/recovery.fstab | grep -w '/cache' | grep /dev | awk '{ print $1 }'");
		String boot=ShellExecuter.command("cat out/etc/recovery.fstab | grep -w '/boot' | grep /dev | awk '{ print $1 }'");
		String recovery=ShellExecuter.command("cat out/etc/recovery.fstab | grep -w '/recovery' | grep /dev | awk '{ print $1 }'");
		String output;
		output=ShellExecuter.CopyRight();
		output+="/boot emmc " +boot + 
				"/recovery emmc " +recovery + 
				"/system ext4 "+system + 
				"/data ext4 "+data + 
				"/cache ext4 " +cache + 
				"/sdcard vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SD\";storage;wipeingui;removable;settingsstorage";
		return output;
	}
	
	private String getMountsMtk() {
		String system=ShellExecuter.command("cat recovery.img-ramdisk/etc/recovery.fstab | grep -w '/system' | awk '{ print $3 }'");
		String data=ShellExecuter.command("cat out/recovery.img-ramdisk/etc/recovery.fstab | grep -w '/data' | awk '{ print $3 }'");
		String cache=ShellExecuter.command("cat out/recovery.img-ramdisk/etc/recovery.fstab | grep -w '/cache' | awk '{ print $3 }'");
		String boot=ShellExecuter.command("cat out/recovery.img-ramdisk/etc/recovery.fstab | grep -w '/boot' | awk '{ print $3 }'");
		String recovery=ShellExecuter.command("cat out/recovery.img-ramdisk/etc/recovery.fstab | grep -w '/recovery' | awk '{ print $3 }'");
		String output;
		output=ShellExecuter.CopyRight();
		output+="/boot emmc " +boot +
				"/recovery emmc " +recovery + 
				"/system ext4 "+system + 
				"/data ext4 "+data + 
				"/cache ext4 " +cache + 
				"/sdcard vfat /dev/block/mmcblk1p1 /dev/block/mmcblk1 flags=display=\"Micro SD\";storage;wipeingui;removable;settingsstorage";
		return output;
	}
	
	private void CheckCompression() {
		String idata=null;
		if(lzma==true)
		{
		System.out.println("using lz4 custom boot  ");
		idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/mkbootimg_lzma.mk";	
		}
		if(lz4==true)
		{
		System.out.println("using lz4 custom boot  ");
		idata+="BOARD_CUSTOM_BOOTIMG_MK := device/generic/twrpbuilder/mkbootimg_lz4.mk";
		}
		if(idata!=null)
		{
			ShellExecuter.command("echo "+idata +" >> " +GetBuildInfo.getCodename()+"/kernel.mk");
		}
	}
}
