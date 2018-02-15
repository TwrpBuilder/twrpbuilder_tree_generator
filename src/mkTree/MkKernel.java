package mkTree;

import java.io.File;
import util.GetBuildInfo;
import util.ShellExecuter;
import util.FWriter;

public class MkKernel {
	private String idata;
	private ShellExecuter shell;
	private GetBuildInfo info;
	
	public MkKernel() {
		System.out.println("Making kernel.mk");
		shell.mkdir("out");
		shell.commandnoapp("chmod 777 umkbootimg");
		shell.command("$(pwd)/umkbootimg -i recovery.img -o out/ ");
		if(new File("out/recovery.img-zImage").exists())
		{
		shell.cp("out/recovery.img-zImage", info.getCodename()+File.separator+"kernel");
		}
		long l=0;
		if(new File("out/recovery.img-dt").length()!=l)
		{
		
			shell.cp("out/recovery.img-dt", info.getCodename()+File.separator+"dt.img");
			new FWriter("kernel.mk",getData(true));
		}else {
			new FWriter("kernel.mk",getData(false));
		}
		
	}
	
	private String getData(boolean dt) {
		String pagesize=ShellExecuter.commandnoapp("cat out/recovery.img-pagesize");
		String cmdline=ShellExecuter.commandnoapp("cat out/recovery.img-cmdline");
		String ramdiskofsset=ShellExecuter.commandnoapp("cat out/recovery.img-ramdisk_offset");
		String tagsoffset=ShellExecuter.commandnoapp("cat out/recovery.img-tags_offset");
		String kernelbase=ShellExecuter.commandnoapp("cat out/recovery.img-base");

		idata =ShellExecuter.CopyRight();
		idata+="# Kernel\n" + 
				"TARGET_PREBUILT_KERNEL := device/"+info.getBrand()+File.separator+info.getCodename()+"/kernel\n" + 
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

	
}
