package util;

import java.io.File;

public class Clean {
	private ShellExecutor shell;
	public Clean(){
		shell=new ShellExecutor();
		shell.commandnoapp("rm -rf build.prop recovery.img " );
		if (file("mounts"))
		{
			shell.commandnoapp("rm mounts ");
		}
		if (file("umkbootimg"))
		{
			shell.commandnoapp("rm umkbootimg");
		}
		
		if (file(Config.outDir))
		{
			shell.commandnoapp("rm -rf "+Config.outDir);
		}
		
		if(file("unpack-MTK.pl"))
		{
			shell.command("rm unpack-MTK.pl");
		}
		if (file("bin") || file("unpackimg.sh"))
		{
			shell.commandnoapp("rm -rf unpackimg.sh bin bin/");
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
