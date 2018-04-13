package com.github.twrpbuilder.util;

import java.io.File;

public class Clean {
	private ShellExecutor shell;
	public Clean(){
		shell=new ShellExecutor();
		file("build.prop" );
		file(Config.recoveryFile);
		file("mounts");
		file("umkbootimg");
		file(Config.outDir);
		file("unpack-MTK.pl");
		file("unpackimg.sh");
		file("bin");
		file("magic");
		file("androidbootimg.magic");

	}

	private boolean file(String name){
		if (new File(name).exists())
		{
			shell.rm(name);
			return true;
		}
		else {
			return false;
		}
	}
}
