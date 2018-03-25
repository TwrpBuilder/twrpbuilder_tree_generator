package util;

import java.io.File;

public class Clean {
	private ShellExecutor shell;
	public Clean(){
		shell=new ShellExecutor();
		shell.commandnoapp("rm -rf build.prop recovery.img " );
		if (new File("mounts").exists())
		{
			shell.commandnoapp("rm mounts ");
		}
		if (new File("umkbootimg").exists())
		{
			shell.commandnoapp("rm umkbootimg");
		}
		
		if (new File(Config.outDir).exists())
		{
			shell.commandnoapp("rm -rf "+Config.outDir);
		}
		
		if(new File("unpack-MTK.pl").exists())
		{
			shell.command("rm unpack-MTK.pl");
		}
	}

}
