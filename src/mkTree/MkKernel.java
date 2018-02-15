package mkTree;

import java.io.File;
import util.GetBuildInfo;
import util.ShellExecuter;
import util.FWriter;

public class MkKernel {
	private String idata;
	private long l=0;
	public MkKernel() {
		System.out.println("Making kernel.mk");
		ShellExecuter.mkdir("out");
		 if(GetBuildInfo.mtk==true)
		 {
			 ShellExecuter.commandnoapp("chmod 777 unpack-MTK.pl && mv unpack-MTK.pl out/ ");
			 ShellExecuter.commandnoapp("cp recovery.img out/recovery.img");
			 ShellExecuter.command("cd out && ./unpack-MTK.pl recovery.img ");
			 if(new File("out/recovery.img-kernel.img").exists())
			 {
				 ShellExecuter.command("cp out/recovery.img-kernel.img "+GetBuildInfo.getCodename()+"/kernel");
			 }
			 if(new File("out/recovery.img-args.txt").exists())
			 {
					new FWriter("kernel.mk",getDataMtk());
			 }
		 }else {
		ShellExecuter.commandnoapp("chmod 777 umkbootimg");
		ShellExecuter.command("$(pwd)/umkbootimg -i recovery.img -o out/ ");
		if(new File("out/recovery.img-zImage").exists())
		{
		ShellExecuter.cp("out/recovery.img-zImage", GetBuildInfo.getCodename()+File.separator+"kernel");
		}
		if(new File("out/recovery.img-dt").length()!=l)
		{
		
			ShellExecuter.cp("out/recovery.img-dt", GetBuildInfo.getCodename()+File.separator+"dt.img");
			new FWriter("kernel.mk",getDataQcom(true));
		}else {
			new FWriter("kernel.mk",getDataQcom(false));
		}
		}
		
	}
	
	private String getDataQcom(boolean dt) {
		String pagesize=ShellExecuter.commandnoapp("cat out/recovery.img-pagesize");
		String cmdline=ShellExecuter.commandnoapp("cat out/recovery.img-cmdline");
		String ramdiskofsset=ShellExecuter.commandnoapp("cat out/recovery.img-ramdisk_offset");
		String tagsoffset=ShellExecuter.commandnoapp("cat out/recovery.img-tags_offset");
		String kernelbase=ShellExecuter.commandnoapp("cat out/recovery.img-base");

		idata =ShellExecuter.CopyRight();
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := device/"+GetBuildInfo.getBrand()+File.separator+GetBuildInfo.getCodename()+"/kernel\n" + 
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
	
	private String getDataMtk() {
		String pagesize=ShellExecuter.commandnoapp("cat out/recovery.img-args.txt | grep pagesize | cut -d x -f 2");
		String ramdiskofsset=ShellExecuter.commandnoapp("cat out/recovery.img-args.txt | grep ramdisk_offset | cut -d x -f 2");
		String tagsoffset=ShellExecuter.commandnoapp("cat out/recovery.img-args.txt | grep tags_offset | cut -d x -f 2");
		String kernelbase=ShellExecuter.commandnoapp("cat out/recovery.img-args.txt | grep base | cut -d x -f 2");

		idata =ShellExecuter.CopyRight();
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := device/"+GetBuildInfo.getBrand()+File.separator+GetBuildInfo.getCodename()+"/kernel\n" + 
				"BOARD_KERNEL_BASE := 0x"+kernelbase+"\n" + 
				"BOARD_KERNEL_PAGESIZE := "+pagesize+"\n";
		idata+="BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x"+ramdiskofsset+" --tags_offset 0x"+tagsoffset;
		return idata;
	}

	
}
