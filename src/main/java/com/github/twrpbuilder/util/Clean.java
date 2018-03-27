package com.github.twrpbuilder.util;

import java.io.File;

public class Clean {
	private ShellExecutor shell;
	public Clean(){
		shell=new ShellExecutor();
		shell.rm("build.prop" );
		shell.rm("recovery.img");
		if (file("mounts"))
		{
			shell.rm("mounts");
		}
		if (file("umkbootimg"))
		{
			shell.rm("umkbootimg");
		}
		
		if (file(Config.outDir))
		{
			shell.rm(Config.outDir);
		}
		
		if(file("unpack-MTK.pl"))
		{
			shell.rm("unpack-MTK.pl");
		}
		if (file("bin") || file("unpackimg.sh"))
		{
			shell.rm("unpackimg.sh");
			shell.rm("bin");
		}
		if (file("magic"))
		{
			shell.rm("magic");
		}
	}

	private boolean file(String name){
		if (new File(name).exists())
		{
			return true;
		}
		else {
			return false;
		}
	}
}
