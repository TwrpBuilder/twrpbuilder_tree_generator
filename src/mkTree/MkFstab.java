package mkTree;

import java.io.File;

import util.Clean;
import util.GetBuildInfo;
import util.ShellExecuter;

public class MkFstab {
	private String compressionType;
	private boolean lz4,lzma;
	
	public MkFstab() {
		System.out.println("Copying fstab");
		compressionType=ShellExecuter.commandnoapp("cd out && file --mime-type recovery.img-ramdisk.* | cut -d / -f 2 | cut -d '-' -f 2");
		if(compressionType.equals("lzma"))
		{
			System.out.println("Found lzma comression in ramdisk");
			ShellExecuter.command("mv out/recovery.img-ramdisk.gz out/recovery.img-ramdisk.lzma && lzma -d out/recovery.img-ramdisk.lzma  && cd out && cpio -i <recovery.img-ramdisk");
			lzma=true;
			CheckCompression();
			lastMessage();
		}else if(compressionType.equals("gzip"))
		{
			System.out.println("Found gzip comression in ramdisk");
			ShellExecuter.command("gzip -d out/recovery.img-ramdisk.gz && cd out && cpio -i <recovery.img-ramdisk");
			lastMessage();
		}
		else if(compressionType.equals("lz4"))
		{
			System.out.println("Found lz4 comression in ramdisk");
		ShellExecuter.command("lz4 -d out/recovery.img-ramdisk.* && cd out && cpio -i <recovery.img-ramdisk");
		lz4=true;
		CheckCompression();
		lastMessage();
		}else {
			new Clean();
			System.out.println("failed to uncompress ramdisk");
			System.exit(0);
		}
		
	}
	
	private void lastMessage() {
		if(new File("out/etc").exists()) {
		ShellExecuter.command("mkdir "+GetBuildInfo.getPathS()+"stock && mv out/etc/* "+GetBuildInfo.getPathS()+"stock/");
		}
		System.out.println("Build fingerPrint: "+GetBuildInfo.getFingerPrint());
		System.out.println("tree ready for "+ GetBuildInfo.getCodename());
		System.out.println((char)27 + "[31m" +"Waring :- Check recovery fstab before build");
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
			ShellExecuter.command("echo "+idata +" >> " +GetBuildInfo.getPath()+"/kernel.mk");
		}
	}
}
